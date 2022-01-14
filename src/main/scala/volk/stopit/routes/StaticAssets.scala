package volk.stopit.routes

import cats.effect.IO
import org.http4s.dsl.Http4sDsl
import org.http4s.{ HttpRoutes, StaticFile }

object StaticAssets {

  private val dsl = new Http4sDsl[IO] {}
  import dsl._

  val route: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case r @ GET -> Root =>
      StaticFile.fromResource("/public/html/index.html", Some(r)).getOrElseF(NotFound())
    case r @ GET -> asset =>
      StaticFile.fromResource("/public/" + asset, Some(r)).getOrElseF(NotFound())
  }

}
