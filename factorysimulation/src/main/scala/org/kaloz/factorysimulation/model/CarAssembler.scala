package org.kaloz.factorysimulation.model

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.typesafe.config.Config
import org.kaloz.factorysimulation.infrastucture.jmx.JMXFactory
import org.kaloz.factorysimulation.model.CarFactory.FactoryEvent

import scala.concurrent.{ExecutionContext, Future}

class CarAssembler(val monitorableActivity: MonitorableActivity, val system: ActorSystem) extends FaultyProducer {

  def assemble(parts: (Seq[Wheel], Coachwork, Engine))(implicit executor: ExecutionContext, log: LoggingAdapter): Future[Car] =
    Future[Car]({
      Thread.sleep(monitorableActivity.getActivityTimeInMillis)
      val car = new Car(parts._1, parts._2, parts._3, faulty(monitorableActivity.getFaultPercentage))
      log.debug(s"Assembled car $car")
      system.eventStream.publish(FactoryEvent("assembled"))
      car
    })
}

object CarAssembler {
  def apply(config: Config, system: ActorSystem) = new CarAssembler(JMXFactory.register("assembly", config), system)
}
