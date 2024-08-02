package graphTransformer.web

import graphTransformer.web.model.EChartsGraph
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.files.staticResourceGetServerEndpoint
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter._
import sttp.tapir.ztapir._
import zio._

trait Endpoints {
  def routes(implicit runtime: Runtime[Any]): List[Router => Route]
}

class EndpointsImpl() extends Endpoints {

  def scripts: List[ZServerEndpoint[Any, ZioStreams]] = List(
    "echarts.min.js",
    "jquery.min.js"
  ).map(s => staticResourceGetServerEndpoint("scripts" / s)(getClass.getClassLoader, s))

  val graphViewEndpoint = endpoint
    .in("view")
    .out(htmlBodyUtf8)

  def graphViewLogic = (_: Unit) => ZIO.succeed(EChartsGraph.sample.asHtml)

  // ----------------------- Routes -------------------------------------------

  def frontendRoutes(implicit runtime: Runtime[Any]) = List(
    graphViewEndpoint.zServerLogic(graphViewLogic)
  ).map(VertxZioServerInterpreter().route(_))

  def scriptsRouts(implicit runtime: Runtime[Any]) = scripts
    .map(VertxZioServerInterpreter().route(_))

  override def routes(implicit runtime: Runtime[Any]): List[Router => Route] =
    frontendRoutes ++ scriptsRouts

}

object EndpointsImpl {
  def live: ULayer[EndpointsImpl] = ZLayer.succeed(new EndpointsImpl())
}
