package volk.stopit.util

import cats.effect.{ IO, Resource }

import java.io.{ BufferedReader, BufferedWriter, File }
import java.nio.file.{ Files, StandardOpenOption }
import scala.jdk.CollectionConverters._

object Utils {

  def getJarDir: String = new File(classOf[UtilsSupportingClass].getProtectionDomain.getCodeSource.getLocation.toURI).getPath

  def getFile(path: String): Option[File] = Option(new File(path)).filter(_.exists())

  def fileWriter(file: File): Resource[IO, BufferedWriter] =
    Resource.make(
      IO(Files.newBufferedWriter(file.toPath, StandardOpenOption.APPEND))
    ) {
      writer =>
        IO {
          writer.flush()
          writer.close()
        }
    }

  def fileReader(file: File): Resource[IO, BufferedReader] =
    Resource.make(
      IO(Files.newBufferedReader(file.toPath))
    ) {
      reader =>
        IO {
          reader.close()
        }
    }

  def readFileRev(parent: File, path: String): IO[List[String]] = for {
    f <- IO(new File(parent, path))
    lines <- fileReader(f).use {
      reader =>
        IO(reader.lines()).map(_.iterator().asScala.toList)
    }
  } yield lines.reverse

  implicit class ChainingOps[A](t: A) {
    import scala.util.chaining._

    def |>[R](func: A => R): R = t.pipe(func)
  }

  trait OK

  /** simply indicates that an operation succeeded */
  case object Ok extends OK

}

private class UtilsSupportingClass {}
