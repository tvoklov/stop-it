package volk.stopit.routes

import cats.effect.{ IO, Ref }
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import volk.stopit.Storage.StorageState
import volk.stopit.util.Config.AppConfig

import java.time.LocalDateTime

object Info {

  private val defaultAppName = "Stop it"

  private val dsl = new Http4sDsl[IO] {}
  import dsl._

  def routes(cfg: AppConfig, fssRef: Ref[IO, StorageState]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "appName" => Ok(cfg.appName.getOrElse(defaultAppName))
    case GET -> Root / "full" =>
      for {
        fss <- fssRef.get
        res <-
          Ok(
            AppInfo(
              cfg.appName.getOrElse(defaultAppName),
              fss.lastDate
            ).asJson.noSpaces
          )
      } yield res
  }

  private case class AppInfo(
      appName: String,
      lastFailDate: Option[LocalDateTime]
  )

}
