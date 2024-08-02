package graphTransformer

import graphTransformer.web.Endpoints
import graphTransformer.web.EndpointsImpl
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter
import sttp.tapir.server.vertx.zio.VertxZioServerInterpreter._
import zio._

object Main extends ZIOAppDefault {
  implicit override val runtime: Runtime[Any] = zio.Runtime.default

  val server = for {
    endpoints <- ZIO.service[Endpoints]
    _ <- ZIO.scoped(
      ZIO.acquireRelease(
        ZIO
          .attempt {
            val vertx  = Vertx.vertx()
            val server = vertx.createHttpServer()
            val router = Router.router(vertx)
            endpoints.routes.foreach(_(router))
            server.requestHandler(router).listen(8080)
          }
          .flatMap(_.asRIO)
      ) { server =>
        ZIO.attempt(server.close()).flatMap(_.asRIO).orDie
      } *> ZIO.never
    )
  } yield ()

  override def run = server.provideLayer(
    EndpointsImpl.live
  )
}
