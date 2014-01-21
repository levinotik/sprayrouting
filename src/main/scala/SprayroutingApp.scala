package com.ln.sprayrouting

import akka.actor.{Actor, ActorSystem, Props, ActorLogging}
import akka.actor.ActorDSL._
import akka.io.IO
import spray.can.Http
import spray.routing._
import akka.io.Tcp._

object SprayroutingApp extends App {
  implicit val system = ActorSystem("sprayrouting-system")

  /* Spray Service */
  val service= system.actorOf(Props[SprayroutingActor], "sprayrouting-service")

  val ioListener = actor("ioListener")(new Act with ActorLogging {
    become {
      case b @ Bound(connection) => log.info(b.toString)
    }
  })


  IO(Http).tell(Http.Bind(service, SprayroutingConfig.HttpConfig.interface, SprayroutingConfig.HttpConfig.port), ioListener)

  println("Hit any key to exit.")
  val result = readLine()
  system.shutdown()
}

class SprayroutingActor extends Actor with SprayroutingService with ActorLogging {
  def actorRefFactory = context
  def receive = runRoute(sprayroutingRoute)
}

trait SprayroutingService extends HttpService {
  implicit def executionContext = actorRefFactory.dispatcher

  val sprayroutingRoute = {
    path("protected") {
      (post & userAuthDirective) {
        authcreds =>
          complete("hi there")
      }
    }
  }

  def userAuthDirective = {
    (headerValueByName("username") & headerValueByName("timestamp") & headerValueByName("uuid")).as(HttpHeaderAuthCreds)
  }

  case class HttpHeaderAuthCreds(username: String, timestamp: Long, uuid: String)

}

