## Car Factory Simulation

This is my solution for **Dev Challenges** series [Car Factory Simulation](CHALLENGE.md) section.

My solution was inspired by [this](http://www.smartjava.org/content/visualizing-back-pressure-and-reactive-streams-akka-streams-statsd-grafana-and-influxdb) blog entry which is about visualising back-pressure and reactive streams.

### Tool set
- [Akka Streams](http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0-RC4/scala.html?_ga=1.167543678.1764891530.1418773886) to have actor and streams support to wire up the factory
- [InfluxDB](https://influxdb.com/) to save time series information of the production line
- [Grafana](http://grafana.org/) to visualise car factory time series
- [Docker](https://www.docker.com/) to wrap services and make them shippable for different environments
- [Docker Compose](https://docs.docker.com/compose/) to be able to start up the entire car factory with 1 single command
- [boot2docker](http://boot2docker.io/) to be able to run Docker on Mac

## How it works?
There are 3 Docker containers involved in the game. 
- **influxdb** is responsible for storing time series informations sent by the **carfactory**. The image is coming form the [Docker Hub](https://hub.docker.com/account/signup/) as a preconfigured influx database (tutum/influxdb:0.8.8). After the database has been started it creates the default database which is **carfactory**. It exposes 8083 as an admin port and 8086 for the REST API calls. 
- **grafana** is responsible for visualising the information stored in **influxdb**. The image is coming form the [Docker Hub](https://hub.docker.com/account/signup/) as a preconfigured grafana application (tutum/grafana). To be able to display what is happanging in the factory you have to import the [factorysimulation_grafana.json](factorysimulation_grafana.json) configuration file. It exposes 80 to be able to open the UI in a browser.
- **carfactory** is modelling the factory using Akka Steams. The Docker image will be built on the fly when you start the application first. The parameters of the ***production line*** can be configured via **jconsole** under the **factory** type. It exposes 1898 to be able to connect to it using jconsole. 

### How to run
If you run Docker on Mac than follow [this](http://viget.com/extend/how-to-use-docker-on-os-x-the-missing-guide) description to prepare your Mac for Docker hosting. 

In the [docker-compose.yml](docker-compose.yml) file modify all the '192.168.59.103' to your own IP which is the virtual box's IP if you on Mac or the host machine IP on linux.
For Mac run the following:
```
boot2docker ip
```

After having a configured and running docker and modified the configuration it is high time to start your factory.
```
docker-compose up -d
```
There will be 3 docker containers running what you can verify with the following command:
```
docker ps
```
You can check the combined log of all the containers.
```
docker-compose logs
```
- Let's log into the **influxdb**. For my setup it is http://192.168.59.103:8083. The credentials are root/root
- Let's verify if **grafana** is running. For my machine it is http://192.168.59.103. The use is admin but you have to check the logs I described before for the password **grafana** expects. After the successfully log in there should be a 'Import' button. You have to import [factorysimulation_grafana.json](factorysimulation_grafana.json) as I explained.

Now you should see the behaviour of your car factory!!

If you would like to play around with the configuration you need **jconsole**. Run the following command and connect to the JMX server.
```
jsonsole
```
```
192.168.59.103:1898
```

If everything went well you should be able to configure your factory and experiment with the configuration.

Have a good fun!!!

## Some extra details
The main player of the factory is **Akka Streams** and the **push-back** approach. Every single activity in the production line has its own activity time to simulate car factory more realistically. Producing a wheel only requires 300 millis but an engine 1 second. Assembling a car 5 seconds. It has a serious consequence. Namely we produce more wheels then we really need at a given time since producing a car significantly slower despite the fact that the car requires 4 wheels. Without push-back there would be an inevitable memory leak. But with the push-back akka manages the internal work load. It stops producing wheels if its not needed or starts again when there is a demand for it. It also applies for any other parts we need for the car.
Interesting scenarios to test:
- Increase the wheel production time be a big time or change the faulty ratio to 100. As a result all the other producers will stop working as the wheel production is a bottleneck. The assembly line is waiting for wheels but the rest of them will reach their buffer capacity so they will just stop producing.
- Modify the assembly line faulty ratio to 100. Every producer in the factory try to generate items but the number of produced cars will drop to 0.
- Increase the production time for every car part meanwhile drop the assembly time to be a marginal compared to them. Assembly line will starve.

The heart of the production line is implemented in the **CarFactory** class. It is easy to understand what is happening and easy to map the programmed flow to the [diagram](pics/DevChallengeCarFactory.jpg).
```scala
    FlowGraph.closed() { implicit builder: FlowGraph.Builder[Unit] =>

      import akka.stream.scaladsl.FlowGraph.Implicits._

      val wheelProduction = Source.actorPublisher[Wheel](ProducerActor.props[Wheel](conf))
      val coachworkProduction = Source.actorPublisher[Coachwork](ProducerActor.props[Coachwork](conf))
      val engineProduction = Source.actorPublisher[Engine](ProducerActor.props[Engine](conf))

      val prepareCarAssembly = builder.add(ZipWith[Seq[Wheel], Coachwork, Engine, (Seq[Wheel], Coachwork, Engine)]((_, _, _)))
      val prepareCarPainting = builder.add(Balance[Car](3))
      val carAssembler = CarAssembler(conf, system)

      def carPainting(color: Color, conf: Config) = {
        val carPainter = CarPainter(color, conf, system)
        Flow[Car].mapAsyncUnordered[Car](conf.getInt("painting.parallel"))(carPainter.paint(_))
      }

      val collect = builder.add(Merge[Car](3))
      val customer = Sink.foreach[Car](car => {
        log.debug(s"Car is done $car")
        system.eventStream.publish(FactoryEvent("done"))
      })

      wheelProduction.filter(!_.faulty).grouped(4)  ~> prepareCarAssembly.in0
      coachworkProduction.filter(!_.faulty)         ~> prepareCarAssembly.in1
      engineProduction.filter(!_.faulty)            ~> prepareCarAssembly.in2

      prepareCarAssembly.out.mapAsyncUnordered[Car](conf.getInt("assembly.parallel"))(carAssembler.assemble(_)).filter(!_.faulty) ~> prepareCarPainting.in

      prepareCarPainting.out(0) ~> carPainting(Blue, conf)    ~> collect ~> customer
      prepareCarPainting.out(1) ~> carPainting(Red, conf)     ~> collect
      prepareCarPainting.out(2) ~> carPainting(Yellow, conf)  ~> collect
    }
```

## Screen shots
### InfluxDB
![Alt text](pics/InfluxDB.png?raw=true "Influx")

### Grafana
![Alt text](pics/Grafana.png?raw=true "Grafana")

### jconsole
![Alt text](pics/jconsole.png?raw=true "jconsole")
