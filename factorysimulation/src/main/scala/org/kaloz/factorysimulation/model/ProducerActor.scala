package org.kaloz.factorysimulation.model

import java.lang.{Boolean => JBoolean}

import akka.actor.{ActorLogging, Props}
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{Cancel, Request}
import com.typesafe.config.Config
import org.kaloz.factorysimulation.infrastucture.jmx.JMXFactory
import org.kaloz.factorysimulation.model.CarFactory.FactoryEvent

import scala.reflect.ClassTag

class ProducerActor[T <: Producible](itemToProduce: ClassTag[T], monitorableActivity: MonitorableActivity) extends ActorPublisher[T] with FaultyProducer with ActorLogging {

  def receive = {
    case Request(cnt) => produceItem
    case Cancel => context.stop(self)
    case _ =>
  }

  private def produceItem {
    while (isActive && totalDemand > 0) {
      Thread.sleep(monitorableActivity.getActivityTimeInMillis())
      val item = itemToProduce.runtimeClass.getConstructor(classOf[Boolean]).newInstance(faulty(monitorableActivity.getFaultPercentage): JBoolean).asInstanceOf[T]
      log.debug(s"Produced car part $item")
      context.system.eventStream.publish(FactoryEvent(item.getClass.getSimpleName.toLowerCase))
      onNext(item)
    }
  }
}

object ProducerActor {
  def props[T <: Producible : Manifest](config: Config) = {
    val simpleName = manifest.runtimeClass.getSimpleName.toLowerCase
    Props(classOf[ProducerActor[T]], manifest, JMXFactory.register(simpleName, config))
  }
}
