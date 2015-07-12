package org.kaloz.factorysimulation.model

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.scaladsl._
import com.typesafe.config.Config

import scala.concurrent.ExecutionContext

object CarFactory {

  case class FactoryEvent(seriesName: String, time: Long = System.currentTimeMillis)

  def wireProductionLine(conf: Config, system: ActorSystem): RunnableGraph[Unit] = {

    val executorService: ExecutorService = Executors.newCachedThreadPool()
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    implicit val log: LoggingAdapter = Logging.getLogger(system, CarFactory)

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
  }
}
