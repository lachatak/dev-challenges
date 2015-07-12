package org.kaloz.factorysimulation


import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.kaloz.factorysimulation.infrastucture.timeseries.InfluxDBActor
import org.kaloz.factorysimulation.model.CarFactory


object ActorFlowGraphFactoryMain extends App {

  val conf = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("carFactory")
  system.actorOf(InfluxDBActor.props(conf))
  val carFactory = CarFactory.wireProductionLine(conf, system)
  implicit val materializer = ActorMaterializer()
  carFactory.run

}





