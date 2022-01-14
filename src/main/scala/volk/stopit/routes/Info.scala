package volk.stopit.routes

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.Http4sDsl
import volk.stopit.util.Config.AppConfig

object Info {

  private val defaultAppName = "Stop it"

  private val dsl = new Http4sDsl[IO] {}
  import dsl._

  def routes(cfg: AppConfig): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "appName" => Ok(cfg.appName.getOrElse(defaultAppName))
  }

}
