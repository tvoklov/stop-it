package volk.stopit.routes

import cats.effect.{ IO, Ref }
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import volk.stopit.storage.Storage.StorageState
import volk.stopit.storage.{ FailLine, Storage }

import java.time.LocalDateTime

object Fail {

  private object LimitParamMatcher  extends QueryParamDecoderMatcher[Int]("limit")
  private object OffsetParamMatcher extends QueryParamDecoderMatcher[Int]("offset")

  private val dsl = new Http4sDsl[IO] {}
  import dsl._

  def routes(fssRef: Ref[IO, StorageState[FailLine]]): HttpRoutes[IO] = {

    def load(limit: Int, offset: Int) = for {
      fss <- fssRef.get
      done <-
        Storage
          .readLinesRevGen[FailLine](offset, limit)(fss)
          .map(_.asJson.noSpaces)
          .flatMap(Ok.apply(_))
    } yield done

    HttpRoutes.of[IO] {
      case GET -> Root / "get" :? LimitParamMatcher(limit) :? OffsetParamMatcher(offset) => load(limit, offset)
      case GET -> Root / "get" :? LimitParamMatcher(limit)                               => load(limit, 0)
      case GET -> Root / "get"                                                           => load(50, 0)

      case r @ POST -> Root / "new" =>
        for {
          fss <- fssRef.get

          flj <- r.as[FailLineJson]
          done <- {
            val json = flj.toFailLine(fss).asJson.noSpaces
            Storage
              .writeLine(json)(fss)
              .flatMap(
                _ => Ok("written")
              )
          }

          _ <- fssRef.update(
            cur =>
              cur.copy(
                lastId = cur.lastId + 1,
                lastDate = Some(LocalDateTime.now())
              )
          )
        } yield done
    }
  }

  private object FailLineJson {
    implicit val decoder: EntityDecoder[IO, FailLineJson] = jsonOf[IO, FailLineJson]
  }

  private case class FailLineJson(reason: Option[String], toWhat: Option[String], satisfied: Option[Boolean]) {

    def toFailLine(ss: StorageState[FailLine]): FailLine = {
      val now = LocalDateTime.now()
      val days = ss.lastDate.fold(0)(
        ld => java.time.temporal.ChronoUnit.DAYS.between(ld, now).toInt.abs
      )

      FailLine(
        ss.lastId + 1,
        now,
        reason.getOrElse(""),
        days,
        toWhat.getOrElse(""),
        satisfied.getOrElse(false)
      )
    }

  }

}
