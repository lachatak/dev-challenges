package org.kaloz.factorysimulation

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink => StreamSink, _}
import org.kaloz.factorysimulation.model._

import scala.concurrent.ExecutionContext
import scalaz.Scalaz._

object SimpleFlowGraphFactoryMain extends App {

  implicit val system: ActorSystem = ActorSystem("carfactory")
  val executorService: ExecutorService = Executors.newCachedThreadPool()
  val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
  val log: LoggingAdapter = Logging.getLogger(system, SimpleFlowGraphFactoryMain)
  implicit val materializer = ActorMaterializer()

  val g = FlowGraph.closed() { implicit builder: FlowGraph.Builder[Unit] =>
    import akka.stream.scaladsl.FlowGraph.Implicits._

    val wheel: Source[Wheel, Unit] = Source(() => Iterator.continually(new Wheel))
    val coachwork: Source[Coachwork, Unit] = Source(() => Iterator.continually(new Coachwork))
    val engine: Source[Engine, Unit] = Source(() => Iterator.continually(new Engine))

    val out = StreamSink.foreach(println)

    val assemble = builder.add(ZipWith[Seq[Wheel], Coachwork, Engine, Car]((wheels, coachwork, engine) => new Car(wheels, coachwork, engine)))
    val painting = builder.add(Balance[Car](3))
    val merge = builder.add(Merge[Car](3))

    val carFlow = Flow[Car]

    wheel.grouped(4)  ~> assemble.in0
    coachwork         ~> assemble.in1
    engine            ~> assemble.in2

    assemble.out ~> painting.in

    painting.out(0) ~> carFlow.map(_.copy(color = Red.some))      ~> merge ~> carFlow.take(4) ~> out
    painting.out(1) ~> carFlow.map(_.copy(color = Blue.some))     ~> merge
    painting.out(2) ~> carFlow.map(_.copy(color = Yellow.some))   ~> merge

  }

  g.run()
}