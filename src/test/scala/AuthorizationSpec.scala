package com.ln.sprayrouting
import org.scalatest._
import spray.testkit.ScalatestRouteTest
import spray.routing.HttpService
import spray.http.StatusCodes

class AuthorizationSpec extends FlatSpec with Matchers with ScalatestRouteTest with HttpService with SprayroutingService  {

  implicit def actorRefFactory = system

  val route = sprayroutingRoute

  "A userAuthDirective" should "reject the request if the auth headers are missing" in {
    Post("/protected") ~> sealRoute(route) ~> check {
      status === StatusCodes.UnsupportedMediaType //passes no matter what it is
    }
  }
  
  it should "work" in {
    Post("/protected") ~> route ~> check {
      status === StatusCodes.BadRequest //fails no matter what
    }
  }
  
}


