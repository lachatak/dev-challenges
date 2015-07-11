package org.kaloz.factorysimulation

import java.lang.management.ManagementFactory
import java.lang.{Boolean => JBoolean}
import java.util.UUID
import java.util.concurrent.{ExecutorService, Executors}
import javax.management.{MBeanServer, ObjectName}

import akka.actor._
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import akka.stream.scaladsl.{Sink => StreamSink, _}
import com.netflix.hystrix.{HystrixCommandGroupKey, HystrixCommand}
import com.netflix.hystrix.HystrixCommand.Setter
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang3.Validate
import play.api.libs.json.Json

import scala.beans.BeanProperty
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.Random
import scalaj.http.{HttpResponse, Http}
import scalaz.Scalaz._

object ActorFlowGraphFactoryMain extends App {

  val conf = ConfigFactory.load()

  val mbs = ManagementFactory.getPlatformMBeanServer

  implicit val system: ActorSystem = ActorSystem("factorysimulator")
  var influxDBActor = system.actorOf(InfluxDBActor.props(conf))
  val executorService: ExecutorService = Executors.newCachedThreadPool()
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
  implicit val log: LoggingAdapter = Logging.getLogger(system, ActorFlowGraphFactoryMain)
  implicit val materializer = ActorMaterializer()

  val g = FlowGraph.closed() { implicit builder: FlowGraph.Builder[Unit] =>

    import akka.stream.scaladsl.FlowGraph.Implicits._

    val wheelProduction = Source.actorPublisher[Wheel](ProductionLineActor.props[Wheel](mbs, conf, influxDBActor))
    val coachworkProduction = Source.actorPublisher[Coachwork](ProductionLineActor.props[Coachwork](mbs, conf, influxDBActor))
    val engineProduction = Source.actorPublisher[Engine](ProductionLineActor.props[Engine](mbs, conf, influxDBActor))

    val prepareCarAssembly = builder.add(ZipWith[Seq[Wheel], Coachwork, Engine, (Seq[Wheel], Coachwork, Engine)]((_, _, _)))
    val prepareCarPainting = builder.add(Balance[Car](3))
    val carAssembler = CarAssembler(mbs, conf, influxDBActor)

    def carPainting(color: Color, conf: Config) = {
      val carPainter = CarPainter(color, mbs, conf, influxDBActor)
      Flow[Car].mapAsyncUnordered[Car](conf.getInt("painting.parallel"))(carPainter.paint(_))
    }

    val collect = builder.add(Merge[Car](3))
    val customer = StreamSink.foreach[Car](car => {
//      log.info(s"done $car")
      influxDBActor ! Event("car-done")
    })

    wheelProduction.filter(!_.faulty).grouped(4) ~> prepareCarAssembly.in0
    coachworkProduction.filter(!_.faulty) ~> prepareCarAssembly.in1
    engineProduction.filter(!_.faulty) ~> prepareCarAssembly.in2

    prepareCarAssembly.out.mapAsyncUnordered[Car](conf.getInt("car.parallel"))(carAssembler.assemble(_)).filter(!_.faulty) ~> prepareCarPainting.in

    prepareCarPainting.out(0) ~> carPainting(Blue, conf) ~> collect ~> customer
    prepareCarPainting.out(1) ~> carPainting(Red, conf) ~> collect
    prepareCarPainting.out(2) ~> carPainting(Yellow, conf) ~> collect

  }

  g.run()
}

case class Event(seriesName: String, time: Long = System.currentTimeMillis)

sealed trait Color

case object Blue extends Color

case object Red extends Color

case object Yellow extends Color

sealed trait Producible {
  val serialNumber = UUID.randomUUID().toString

  def faulty: Boolean

  override def toString() = s"""{"name":"${this.getClass.getSimpleName.toLowerCase}", "serialNumber":"$serialNumber", "faulty":"$faulty"}"""
}

case class Wheel(faulty: Boolean) extends Producible

case class Coachwork(faulty: Boolean) extends Producible

case class Engine(faulty: Boolean) extends Producible

case class Car(wheels: Seq[Wheel], coachwork: Coachwork, engine: Engine, faulty: Boolean, color: Option[Color] = None) extends Producible {

  Validate.isTrue(wheels.size == 4)

  override def toString() = s"""{"name":"${this.getClass.getSimpleName.toLowerCase}", "serialNumber":"$serialNumber", "wheels":[$wheels], "coachwork":"$coachwork", "engine":"$engine", "color":"$color", "faulty":"$faulty"}"""
}


class ProductionLineActor[T <: Producible](itemToProduce: ClassTag[T], monitorableActivity: MonitorableActivityMXBean, influxDBActor: ActorRef) extends ActorPublisher[T] with ActorLogging {

  def receive = {
    case Request(cnt) => produceItem
    case Cancel => context.stop(self)
    case _ =>
  }

  private def produceItem {
    while (isActive && totalDemand > 0) {
      Thread.sleep(monitorableActivity.getActivityTimeInMillis())
      val item = itemToProduce.runtimeClass.getConstructor(classOf[Boolean]).newInstance(faulty: JBoolean).asInstanceOf[T]
//      log.info(s"produced part $item")
      influxDBActor ! Event(item.getClass.getSimpleName.toLowerCase)
      onNext(item)
    }
  }

  private def faulty: Boolean = Random.nextInt(101) < monitorableActivity.getFaultPercentage()
}

object ProductionLineActor {
  def props[T <: Producible : Manifest](mbeanServer: MBeanServer, config: Config, influxDBActor: ActorRef) = {
    val simpleName = manifest.runtimeClass.getSimpleName.toLowerCase
    val productionTimeInMillis = config.getLong(s"$simpleName.production.time.in.millis")
    val faultPercentage = config.getInt(s"$simpleName.fault.percentage")
    val name = new ObjectName(s"factory:type=$simpleName")
    val mbean = new MonitorableActivityMXBeanImpl(productionTimeInMillis, faultPercentage)
    mbeanServer.registerMBean(mbean, name)
    Props(classOf[ProductionLineActor[T]], manifest, mbean, influxDBActor)
  }
}

class CarAssembler(val mbeanServer: MBeanServer, val config: Config, influxDBActor: ActorRef) {

  val paintingTimeInMillis = config.getLong("car.production.time.in.millis")
  val faultPercentage = config.getInt("car.fault.percentage")
  val name = new ObjectName("factory:type=assembly")
  val mbean = new MonitorableActivityMXBeanImpl(paintingTimeInMillis, faultPercentage)
  mbeanServer.registerMBean(mbean, name)

  def assemble(parts: (Seq[Wheel], Coachwork, Engine))(implicit executor: scala.concurrent.ExecutionContext, log: LoggingAdapter): Future[Car] =
    Future[Car]({
      Thread.sleep(mbean.getActivityTimeInMillis)
      val car = new Car(parts._1, parts._2, parts._3, Random.nextInt(101) < mbean.getFaultPercentage)
//      log.info(s"produced car $car")
      influxDBActor ! Event("assembled")
      car
    })
}

object CarAssembler {
  def apply(mbeanServer: MBeanServer, config: Config, influxDBActor: ActorRef) = new CarAssembler(mbeanServer, config, influxDBActor)
}

class CarPainter(val color: Color, val mbeanServer: MBeanServer, val config: Config, influxDBActor: ActorRef) {

  val colorStr = color.getClass.getSimpleName.toLowerCase.toList.reverse.tail.reverse.mkString
  val paintingTimeInMillis = config.getLong(s"painting.$colorStr.time.in.millis")
  val faultPercentage = config.getInt(s"painting.$colorStr.fault.percentage")
  val name = new ObjectName(s"factory:type=$colorStr")
  val mbean = new MonitorableActivityMXBeanImpl(paintingTimeInMillis, faultPercentage)
  mbeanServer.registerMBean(mbean, name)

  def paint(car: Car)(implicit executor: scala.concurrent.ExecutionContext, log: LoggingAdapter): Future[Car] =
    Future[Car]({
      Thread.sleep(mbean.getActivityTimeInMillis)
      val newCar = car.copy(color = color.some, faulty = Random.nextInt(101) < mbean.getFaultPercentage)
//      log.info(s"painted car $newCar")
      influxDBActor ! Event(colorStr)
      newCar
    })
}

object CarPainter {
  def apply(color: Color, mbeanServer: MBeanServer, config: Config, influxDBActor: ActorRef) = new CarPainter(color, mbeanServer, config, influxDBActor)
}

trait MonitorableActivityMXBean {
  def getActivityTimeInMillis(): Long

  def setActivityTimeInMillis(time: Long)

  def getFaultPercentage(): Int

  def setFaultPercentage(percentage: Int)

}

class MonitorableActivityMXBeanImpl(@BeanProperty var activityTimeInMillis: Long, @BeanProperty var faultPercentage: Int) extends MonitorableActivityMXBean

class InfluxDBActor(conf: Config) extends Actor with ActorLogging {

  val hystrixRequestContext = HystrixRequestContext.initializeContext();
  val host = conf.getString("influx.host")
  val port = conf.getInt("influx.port")
  val user = conf.getString("influx.user")
  val password = conf.getString("influx.password")
  val db = conf.getString("influx.db")

  val url = s"http://$host:$port/db/$db/series?u=$user&p=$password"

  println(url)

  def receive = {
    case Event(seriesName, timestamp) => sendToInfluxDB(convertToInfluxDBJson(seriesName, timestamp))
  }

  def convertToInfluxDBJson(seriesName: String, timestamp: Long) = Json.prettyPrint(Json.arr(
    Json.obj(
      "name" -> seriesName,
      "columns" -> Json.arr("time", "value"),
      "points" -> Json.arr(Json.arr(timestamp, 1))
    ))
  )

  def sendToInfluxDB(msg: String) = new PostDataCommand(url, msg).execute()

  class PostDataCommand(val url:String, val message:String) extends HystrixCommand[HttpResponse[String]](Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Stat"))){
    override def run(): HttpResponse[String] = {
      Http(url).postData(message).header("content-type", "application/json").asString
    }
  }
}

object InfluxDBActor {
  def props(config: Config) = Props(classOf[InfluxDBActor], config)
}