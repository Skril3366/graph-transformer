package graphTransformer.web

import java.time.Instant

import graphTransformer.web.model.DummyResponse
import graphTransformer.web.model.Nested
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.plainBody
import sttp.tapir.query
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter._
import sttp.tapir.ztapir._
import sttp.tapir.ztapir.resourceGetServerEndpoint
import zio._

trait Endpoints {
  def routes(implicit runtime: Runtime[Any]): List[Router => Route]
}

class EndpointsImpl() extends Endpoints {

  def mainPage: ZServerEndpoint[Any, ZioStreams] = resourceGetServerEndpoint("view")(
    getClass.getClassLoader,
    "graph.html"
  )

  def scripts: List[ZServerEndpoint[Any, ZioStreams]] = List(
    "echarts.min.js",
    "jquery.min.js"
  ).map(s => resourceGetServerEndpoint("scripts" / s)(getClass.getClassLoader, s))

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
  def apiRoutes(implicit runtime: Runtime[Any]) = List(
    dummyRoute.zServerLogic(dummyLogic),
    otherDummyRoute.zServerLogic(otherDummyLogic),
  ).map(VertxZioServerInterpreter().route(_))

  def resources(implicit runtime: Runtime[Any]) = (scripts :+ mainPage)
    .map(VertxZioServerInterpreter().route(_))

  override def routes(implicit runtime: Runtime[Any]): List[Router => Route] =
    apiRoutes ++ resources

}

object EndpointsImpl {
  def live: ULayer[EndpointsImpl] = ZLayer.succeed(new EndpointsImpl())
}
