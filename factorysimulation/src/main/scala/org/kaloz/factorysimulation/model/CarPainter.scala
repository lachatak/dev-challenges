package org.kaloz.factorysimulation.model

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import com.typesafe.config.Config
import org.kaloz.factorysimulation.infrastucture.jmx.JMXFactory
import org.kaloz.factorysimulation.model.CarFactory.FactoryEvent

import scala.concurrent.Future
import scalaz.Scalaz._

class CarPainter(val color: Color, val monitorableActivity: MonitorableActivity, val system: ActorSystem) extends FaultyProducer {

  val colorStr = color.getClass.getSimpleName.toLowerCase.toList.reverse.tail.reverse.mkString

  def paint(car: Car)(implicit executor: scala.concurrent.ExecutionContext, log: LoggingAdapter): Future[Car] =
    Future[Car]({
      Thread.sleep(monitorableActivity.getActivityTimeInMillis)
      val newCar = car.copy(color = color.some, faulty = faulty(monitorableActivity.getFaultPercentage))
      log.debug(s"Painted to $colorStr car $newCar")
      system.eventStream.publish(FactoryEvent(colorStr))
      newCar
    })
}

object CarPainter {
  def apply(color: Color, config: Config, system: ActorSystem) = new CarPainter(color, JMXFactory.register(s"painting.${color.getClass.getSimpleName.toLowerCase.toList.reverse.tail.reverse.mkString}", config), system)
}
