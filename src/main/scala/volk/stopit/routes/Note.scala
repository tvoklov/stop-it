package volk.stopit.routes

import cats.effect.{ IO, Ref }
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.{ EntityDecoder, HttpRoutes }
import volk.stopit.storage.Storage.StorageState
import volk.stopit.storage.{ FailLine, NoteLine, Storage }

import java.time.LocalDateTime

object Note {

  private object LimitParamMatcher  extends QueryParamDecoderMatcher[Int]("limit")
  private object OffsetParamMatcher extends QueryParamDecoderMatcher[Int]("offset")

  private val dsl = new Http4sDsl[IO] {}
  import dsl._

  def routes(fssRef: Ref[IO, StorageState[FailLine]], nssRef: Ref[IO, StorageState[NoteLine]]): HttpRoutes[IO] = {
    def load(limit: Int, offset: Int) =
      for {
        fss <- nssRef.get
        done <-
          Storage
            .readLinesRevGen[NoteLine](offset, limit)(fss)
            .map(_.asJson.noSpaces)
            .flatMap(Ok.apply(_))
      } yield done

    HttpRoutes.of[IO] {
      case GET -> Root / "get" :? LimitParamMatcher(limit) :? OffsetParamMatcher(offset) => load(limit, offset)
      case GET -> Root / "get" :? LimitParamMatcher(limit)                               => load(limit, 0)
      case GET -> Root / "get"                                                           => load(50, 0)

      case r @ POST -> Root / "new" =>
        for {
          nss <- nssRef.get
          fss <- fssRef.get

          nlj <- r.as[NoteLineJson]
          done <- {
            val json = nlj.toNoteLine(nss, fss).asJson.noSpaces
            Storage
              .writeLine(json)(nss)
              .flatMap(
                _ => Ok("written")
              )
          }

          _ <- nssRef.update(
            cur => cur.copy(lastId = cur.lastId + 1)
          )
        } yield done
    }
  }

  def notOn: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case _ => NotImplemented("Notes are not turned on in config")
  }

  private object NoteLineJson {
    implicit val decoder: EntityDecoder[IO, NoteLineJson] = jsonOf[IO, NoteLineJson]
  }

  private case class NoteLineJson(note: String) {

    def toNoteLine(ss: StorageState[NoteLine], fss: StorageState[FailLine]): NoteLine = {
      val now = LocalDateTime.now()
      val days = fss.lastDate.fold(0)(
        ld => java.time.temporal.ChronoUnit.DAYS.between(ld, now).toInt.abs
      )

      NoteLine(
        ss.lastId + 1,
        now,
        days,
        note
      )
    }

  }

}
