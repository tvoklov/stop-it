package volk.stopit.storage

import cats.effect.IO
import io.circe.Decoder
import io.circe.parser.decode
import volk.stopit.util.Config.StorageConfig
import volk.stopit.util.Utils
import volk.stopit.util.Utils.{ fileWriter, readFileRev, OK, Ok }

import java.io.File
import java.time.LocalDateTime
import scala.util.chaining.scalaUtilChainingOps

object Storage {

  object StorageState {

    def getFromLastElem[T, X](file: File)(whatToReturn: T => X)(implicit decoder: Decoder[T]): IO[Option[X]] =
      for {
        files <- IO(file.list())
        maybeLastFile = files.toList.sorted(Ordering.String.reverse).headOption
        lastTuple <- maybeLastFile match {
          case Some(fileName) =>
            for {
              fullFile <- readFileRev(file, fileName)
            } yield fullFile
              .find(_.nonEmpty)
              .flatMap(decode[T](_).toOption)
              .map(
                fl => whatToReturn(fl)
              )

          case None => IO.pure(None)
        }
      } yield lastTuple

    def from[T <: Storable](sc: StorageConfig)(implicit decoder: Decoder[T]): IO[StorageState[T]] =
      for {
        file <- sc.path.getOrElse(defaultStoragePath).pipe(new File(_)).pipe(IO(_))
        ss <-
          if (file.isFile) IO.raiseError(new IllegalArgumentException("storage path is not a directory"))
          else
            for {
              _ <- IO(if (!file.exists()) file.mkdirs())
              lastThings <- getFromLastElem[T, (Long, Option[LocalDateTime])](file)(
                el => (el.id, Some(el.date))
              )
              (lastId, lastDate) = lastThings.getOrElse((0L, None))
            } yield StorageState[T](lastId, file, lastDate, sc)
      } yield ss
  }

  case class StorageState[T <: Storable](
      lastId: Long,
      path: File,
      lastDate: Option[LocalDateTime],
      config: StorageConfig
  )

  def listDir(path: File): IO[List[String]] =
    for { files <- IO(path.list()) } yield files.toList.sorted(Ordering.String.reverse)

  val defaultStoragePath: String = Utils.getJarDir + File.separator + "storage"

  def readLinesRevGen[T](offset: Int, limit: Int)(ss: StorageState[_])(implicit decoder: Decoder[T]): IO[List[T]] = {
    type FileName = String
    type Line     = String

    def go(offsetR: Int, limitR: Int, fileCIO: IO[List[Line]], fileR: List[FileName]): IO[List[T]] =
      if (limitR <= 0) IO.pure(Nil)
      else
        for {
          fileC <- fileCIO
          dd <-
            fileR match {
              case Nil => IO.pure(fileC.slice(offsetR, offsetR + limitR).flatMap(decode[T](_).toOption))
              case next :: xs =>
                val fileSize = fileC.size
                for {
                  rest <- go(offsetR - fileSize, limitR - fileSize, readFileRev(ss.path, next), xs)
                } yield fileC
                  .slice(offsetR, offsetR + limitR)
                  .flatMap(decode[T](_).toOption) ++ rest
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

  def writeLine(line: String)(ss: StorageState[_]): IO[OK] = {
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
