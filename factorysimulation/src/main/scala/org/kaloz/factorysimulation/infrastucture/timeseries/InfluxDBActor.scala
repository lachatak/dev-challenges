package org.kaloz.factorysimulation.infrastucture.timeseries

import akka.actor.Actor.emptyBehavior
import akka.actor.{Actor, ActorLogging, Props}
import com.netflix.hystrix.HystrixCommand.Setter
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext
import com.netflix.hystrix.{HystrixCommand, HystrixCommandGroupKey}
import com.typesafe.config.Config
import org.kaloz.factorysimulation.infrastucture.timeseries.InfluxDBActor.Flush
import org.kaloz.factorysimulation.model.CarFactory.FactoryEvent
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scalaj.http.{Http, HttpResponse}
import scalaz.Scalaz._

class InfluxDBActor(conf: Config) extends Actor with ActorLogging {

  val hystrixRequestContext = HystrixRequestContext.initializeContext()
  val host = conf.getString("influx.host")
  val port = conf.getInt("influx.port")
  val user = conf.getString("influx.user")
  val password = conf.getString("influx.password")
  val db = conf.getString("influx.db")
  val refreshTime = conf.getInt("influx.send.interval.in.seconds")
  val url = s"http://$host:$port/db/$db/series?u=$user&p=$password"

  context.become(receiveEvent())
  context.system.scheduler.schedule(refreshTime second, refreshTime second, self, Flush)

  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[FactoryEvent])
  }

  def receive = emptyBehavior

  def receiveEvent(events: Map[String, List[Long]] = Map.empty): Receive = {
    case FactoryEvent(seriesName, timestamp) => {
      log.debug(events.toString)
      context.become(receiveEvent(events |+| Map(seriesName -> List(timestamp))))
    }
    case Flush => {
      events.foreach(series => sendToInfluxDB(convertToInfluxDBJson(series._1, series._2)))
      context.become(receiveEvent())
    }
  }

  def convertToInfluxDBJson(seriesName: String, timestamps: List[Long]) = Json.prettyPrint(Json.arr(
    Json.obj(
      "name" -> seriesName,
      "columns" -> Json.arr("time", "value"),
      "points" -> timestamps.map(Json.arr(_, 1))
    ))
  )

  def sendToInfluxDB(msg: String) = new PostDataCommand(url, msg).execute()

  class PostDataCommand(val url: String, val message: String) extends HystrixCommand[HttpResponse[String]](Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Stats"))) {
    override def run(): HttpResponse[String] = {
      Http(url).postData(message).header("content-type", "application/json").asString
    }
  }

}

object InfluxDBActor {
  def props(config: Config) = Props(classOf[InfluxDBActor], config)

  case object Flush

}
