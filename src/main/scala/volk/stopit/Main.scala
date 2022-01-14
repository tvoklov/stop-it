package volk.stopit

import cats.effect._
import cats.syntax.all._
import com.comcast.ip4s.{Host, IpAddress, Port}
import fs2.Stream
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import volk.stopit.Storage.StorageState
import volk.stopit.util.Config

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      cfg <- Config.cfg
      fss <- Ref.ofEffect(StorageState.from(cfg.fails))

      httpApp = Router(
        "/api" -> routes.API.routes(fss),
        "info" -> routes.Info.routes(cfg),
        "/"    -> routes.StaticAssets.route
      ).orNotFound

      serverBuilder = EmberServerBuilder
        .default[IO]
        // don't crucify me for the gets
        .withPort(Port.fromInt(8080).get)
        .withHost(Host.fromString("0.0.0.0").get)
        .withHttpApp(httpApp)

      exitCode <- Stream
        .resource(
          serverBuilder.build >> Resource.eval(Async[IO].never)
        )
        .compile
        .drain
        .as(ExitCode.Success)
    } yield exitCode

}
