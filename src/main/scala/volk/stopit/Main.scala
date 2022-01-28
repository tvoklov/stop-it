package volk.stopit

import cats.effect._
import cats.syntax.all._
import com.comcast.ip4s.{ Host, Port }
import fs2.Stream
import io.circe.generic.auto._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import volk.stopit.storage.Storage.StorageState
import volk.stopit.storage.{ FailLine, NoteLine }
import volk.stopit.util.Config

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    for {
      cfg <- Config.cfg

      fss <- Ref.ofEffect(StorageState.from[FailLine](cfg.fails))
      nss <- cfg.notes match {
        case Some(c) => Ref.ofEffect(StorageState.from[NoteLine](c)).map(Some(_))
        case None    => IO.pure(None)
      }

      _ = println(cfg)

      port <-
        Port
          .fromString(cfg.port.getOrElse("8080"))
          .map(IO.pure)
          .getOrElse(
            IO.raiseError(new IllegalArgumentException("Wrong port in config."))
          )

      host <-
        Host
          .fromString("0.0.0.0")
          .map(IO.pure)
          .getOrElse(IO.raiseError(new IllegalArgumentException("Wrong host.")))

      httpApp = Router(
        "/api/fail" -> routes.Fail.routes(fss),
        "/api/note" -> nss.fold(routes.Note.notOn)(routes.Note.routes(fss, _)),
        "/info"     -> routes.Info.routes(cfg, fss),
        "/"         -> routes.StaticAssets.route
      ).orNotFound

      serverBuilder = EmberServerBuilder
        .default[IO]
        .withPort(port)
        .withHost(host)
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
