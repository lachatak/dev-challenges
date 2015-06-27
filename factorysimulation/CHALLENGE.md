##Challenge 3 - Parallel programming##
Multi-core leveraging programming styles, languages and frameworks are the rage nowadays, and developers can choose from a pool of radically different solutions. Let's play with them by solving a simple problem with different approaches!
##The Challenge##
The task is to ***simulate a car factory*** that has multiple, independent workers connected to each other by conveyor belts. The stages of the process:
The Factory has ***3 in-queues***, one for engines, one for coachworks and one for wheels arriving into the Factory continuously. 
- Workers to ***filter out the faulty parts*** operate on each in-queue.
- The intact parts are carried further on conveyor belts to a worker that assembles a car from each 'coachwork + engine + 4 wheels' combination. 
- The cars then are then moved to a worker that ***randomly puts each onto one of the conveyor belts*** towards 3 painter workers.
- Finally ***all the conveyors from the painter stations ebb into one***, the out-queue of the factory.
The Challenge is (minor) part a modelling task, (major) part a parallel programming exercise. All the workers work independently and leveraging the parallel nature of the factory-process higher throughput is achievable than with a sequential solution. 
**The aim is to maximize the number of cars produced in a fixed amount of time.**

![Alt text](pics/DevChallengeCarFactory.jpg?raw=true "CarFactory")
###Workers### 
- WS-FE: filters out faulty engines. Only "healthy" ones are let trough.
- WS-FC: filters out faulty coachworks
- WS-FW: filters out faulty wheels
- W-CA: assembles cars from each 'coachwork + engine + 4 wheels' combination
- W-S: puts incoming cars on a random out-queue
- W-PR: paints cars to red
- W-PB: paints cars to blue
- W-PG: paints cars to green
- W-M: merges the 3 conveyor belts into one

For the simulation you should implement the following as concurrent, independent processes
- the Factory, obviously
- a Consumer that will consume the cars rolling out of the Factory and keeps counting them to measure the throughput of the Factory
- 3 Providers to supply continuous streams of engines, wheels and coachworks, respectively

##Hints##
the domain model of the factory (Engine, Wheel, Coachwork, Car, Workers) should be as simple as possible as they only provide the pretext for the challenge. Faulty parts could be simply marked with a flag and painting could be simply setting a field. The emphasis should be on the parallelization. The chosen approach (see next section) will probably heavily influence the model.
To simulate CPU-intensive work at the workers simply run a fixed-length for-loop to increment a number.
###An example Domain###
(Yielding to the public demand) Just to demonstrate the idea here is an example model in json 
- wheel: {"type":"wheel", "serialnumber": 4566767, faulty: "true"}
- coachwork: {"type":"coachwork", "serialnumber": 657069978, faulty: "false"}
- engine: {"type":"engine", "serialnumber": 75072345, faulty: "true"}
- car
- 
###Car###
```json
{"wheel": {"type":"wheel", "serialnumber": 4566767},
 "coachwork": {"type":"coachwork", "serialnumber": 657069978},
 "engine": {"type":"engine", "serialnumber": 75072345},
 "serialnumber": 3245554,
 "color": "blue"
```
##Possible approaches/technologies to use##
This problem can be tackled using very different approaches providing an opportunity to compare various styles. Some examples are mentioned below. If you are familiar with either of those, it's not difficult to implement. If you find it too easy or quick, we'd advise you to do it with multiple approaches and compare themas completely different models could emerge. In a CSP or queue-based solution the conveyor belts will be as much as building blocks of the model as the workers. In case of FRP (streams) the workers might simply be stream operations and the conveyor belts are the model's first-class citizens. In an Actor framework the emphasis is on the workers, which send messages to each other, eliminating the notion of conveyors completely.
- CSP (Communicating sequential processes): e.g. Go, Clojure, Java CSP, ...
- Actor model: e.g. Akka (Scala/Java)
- Dataflow Programming/Reactive Programming: e.g. Scala/Java 8 streams, RxJava/RxScala/RxClojure/RxJS/...
- Fork-Join framework of Java 7

##Caveats##
The Factory should be prepared for high load. What if parts coming faster than it can process them (4 wheels for one car)?
