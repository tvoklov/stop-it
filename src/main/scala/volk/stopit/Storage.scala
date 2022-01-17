package volk.stopit

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import volk.stopit.util.Config.StorageConfig
import volk.stopit.util.Utils
import volk.stopit.util.Utils.{readFileRev, _}

import java.io.File
import java.time.LocalDateTime
import scala.util.chaining.scalaUtilChainingOps

object Storage {

  object StorageState {

    def getLastThings(file: File): IO[(Int, Option[LocalDateTime])] =
      for {
        files <- IO(file.list())
        maybeLastFile = files.toList.sorted(Ordering.String.reverse).headOption
        lastTuple <- maybeLastFile match {
          case Some(fileName) =>
            for {
              fullFile <- readFileRev(file, fileName)
            } yield fullFile
              .find(_.nonEmpty)
              .flatMap(decode[FailLine](_).toOption)
              .map(
                fl => (fl.id, Some(fl.date))
              )
              .getOrElse((0, None))

          case None => IO.pure((0, None))
        }
      } yield lastTuple

    def from: StorageConfig => IO[StorageState] = {
      case sc @ StorageConfig(path, _) =>
        for {
          file <- path.getOrElse(defaultStoragePath).pipe(new File(_)).pipe(IO(_))
          ss <-
            if (file.isFile) IO.raiseError(new IllegalArgumentException("storage path is not a directory"))
            else
              for {
                _          <- IO(if (!file.exists()) file.mkdirs())
                lastThings <- getLastThings(file)
                (lastId, lastDate) = lastThings
              } yield StorageState(lastId, file, lastDate, sc)
        } yield ss
    }
  }

  case class StorageState(
      lastId: Int,
      path: File,
      lastDate: Option[LocalDateTime],
      config: StorageConfig
  )

  def listDir(path: File): IO[List[String]] =
    for { files <- IO(path.list()) } yield files.toList.sorted(Ordering.String.reverse)

  val defaultStoragePath: String = Utils.getJarDir + File.separator + "storage"

  case class FailLine(id: Int, date: LocalDateTime, reason: String, prevDayCount: Int, toWhat: String, satisfied: Boolean) {
    def toJson: String = this.asJson.toString()
  }

  def readLinesRev(offset: Int, limit: Int)(ss: StorageState): IO[List[FailLine]] = {
    type FileName = String
    type Line     = String

    def go(offsetR: Int, limitR: Int, fileCIO: IO[List[Line]], fileR: List[FileName]): IO[List[FailLine]] =
      if (limitR <= 0) IO.pure(Nil)
      else
        for {
          fileC <- fileCIO
          dd <-
            fileR match {
              case Nil => IO.pure(fileC.slice(offsetR, offsetR + limitR).flatMap(decode[FailLine](_).toOption))
              case next :: xs =>
                val fileSize = fileC.size
                for {
                  rest <- go(offsetR - fileSize, limitR - fileSize, readFileRev(ss.path, next), xs)
                } yield fileC
                  .slice(offsetR, offsetR + limitR)
                  .flatMap(decode[FailLine](_).toOption) ++ rest
            }
        } yield dd

    for {
      files <- listDir(ss.path)
      lines <-
        files match {
          case Nil     => IO.pure(Nil)
          case x :: xs => go(offset, limit, readFileRev(ss.path, x), xs)
        }
    } yield lines
  }

  def writeLine(line: String)(ss: StorageState): IO[OK] = {
    def newFile(oldName: String = "0.json"): IO[File] = {
      val newCount = oldName.split("\\.").headOption.flatMap(_.toIntOption).getOrElse(0) + 1
      val newName  = s"$newCount.json"

      for {
        f <- IO(new File(ss.path, newName))
        _ <- IO(f.createNewFile())
      } yield f
    }

    for {
      fs <- listDir(ss.path)
      maybeLastFile = fs.headOption

      fileToWriteTo <-
        maybeLastFile match {
          case None => newFile()
          case Some(fn) =>
            for {
              f      <- IO(new File(ss.path, fn))
              length <- IO(f.length())
              ftwt <-
                if (length > 1000000)
                  newFile(fn) // if file is larger than 100 mb, create new file
                else IO.pure(f)
            } yield ftwt
        }

      _ <- fileWriter(fileToWriteTo).use {
        writer =>
          IO {
            writer.write(line + "\n")
            writer.flush()
          }
      }
    } yield Ok
  }

}
