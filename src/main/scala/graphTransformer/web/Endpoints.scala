package graphTransformer.web

import java.time.Instant

import graphTransformer.web.model.DummyResponse
import graphTransformer.web.model.Nested
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.plainBody
import sttp.tapir.query
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter._
import sttp.tapir.ztapir._
import zio._

trait Endpoints {
  def routes(implicit runtime: Runtime[Any]): List[Router => Route]
}

class EndpointsImpl() extends Endpoints {

  // Dummy route
  val dummyRoute =
    endpoint
      .in("dummy")
      .out(jsonBody[DummyResponse])

  def dummyLogic = (_: Unit) => ZIO.succeed(DummyResponse("dummy", Nested(Instant.now)))

  // Other dummy route

  val otherDummyRoute =
    endpoint
      .in("otherDummy")
      .in(query[String]("key"))
      .out(plainBody[String])

  def otherDummyLogic(key: String) = ZIO.succeed(key)

  // Routes

  override def routes(implicit runtime: Runtime[Any]): List[Router => Route] =
    List(
      dummyRoute.zServerLogic(dummyLogic),
      otherDummyRoute.zServerLogic(otherDummyLogic)
    ).map(VertxZioServerInterpreter().route(_))

}

object EndpointsImpl {
  def live: ULayer[EndpointsImpl] = ZLayer.succeed(new EndpointsImpl())
}
