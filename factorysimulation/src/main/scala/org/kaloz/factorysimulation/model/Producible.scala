package org.kaloz.factorysimulation.model

import java.util.UUID

import org.apache.commons.lang3.Validate
import play.api.libs.json.Json
import play.api.libs.json.Json.JsValueWrapper

import scala.util.Random

object Producible2JsValueWrapper {
  implicit def producible2JsValueWrapper(producible: Producible): JsValueWrapper = producible.jsonObj
}

sealed trait Producible {
  val serialNumber = UUID.randomUUID().toString

  def faulty: Boolean

  override def toString() = Json.prettyPrint(jsonObj)

  def jsonObj = Json.obj(
    "name" -> this.getClass.getSimpleName.toLowerCase,
    "serialNumber" -> serialNumber,
    "faulty" -> faulty
  )
}

case class Wheel(faulty: Boolean = false) extends Producible

case class Coachwork(faulty: Boolean = false) extends Producible

case class Engine(faulty: Boolean = false) extends Producible

case class Car(wheels: Seq[Wheel], coachwork: Coachwork, engine: Engine, faulty: Boolean = false, color: Option[Color] = None) extends Producible {


  Validate.isTrue(wheels.size == 4)

  override def toString() = {
    import org.kaloz.factorysimulation.model.Producible2JsValueWrapper._
    Json.prettyPrint(
      Json.obj(
        "name" -> this.getClass.getSimpleName.toLowerCase,
        "serialNumber" -> serialNumber,
        "coachwork" -> coachwork,
        "wheels" -> wheels.map(x => x.jsonObj),
        "engine" -> engine,
        "color" -> color.toString,
        "faulty" -> faulty
      ))
  }
}

trait FaultyProducer {
  def faulty(faultLimit: Integer): Boolean = Random.nextInt(101) < faultLimit
}
