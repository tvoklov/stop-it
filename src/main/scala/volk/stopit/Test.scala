//package volk.stopit
//
//import cats.effect.{IO, IOApp}
//import io.circe.generic.auto._
//import io.circe.parser._
//import io.circe.syntax._
//import volk.stopit.Storage.{FailLine, StorageState}
//import volk.stopit.util.Config
//
//import java.time.LocalDateTime
//
//// just a thing i use for testing that my code worked, don't worry about it
//
//object Test extends IOApp.Simple {
//  override def run: IO[Unit] = for {
//    cfg <- Config.cfg
//    _ = println(cfg)
//    fss <- StorageState.from(cfg.fails)
//    _ = println(fss)
//    _ <- Storage.writeLine(FailLine(1, LocalDateTime.now(), "i don't know", 0, "you", false).asJson.noSpaces)(fss)
//    _ <- Storage.writeLine(FailLine(1, LocalDateTime.now(), "i don't know", 0, "you", false).asJson.noSpaces)(fss)
//    _ <- Storage.writeLine(FailLine(1, LocalDateTime.now(), "i don't know", 0, "you", false).asJson.noSpaces)(fss)
//    _ <- Storage.writeLine(FailLine(1, LocalDateTime.now(), "i don't know", 0, "you", false).asJson.noSpaces)(fss)
//
//    jkkej <- Storage.readLinesRev(2, 10)(fss)
//    _ <- IO(println(jkkej.size))
//    _ <- IO(println(jkkej.mkString("\n")))
//  } yield 1
//}
//
//object HuhJson extends App {
//
//  val json = """{"id":1,"date":"2022-01-13T17:12:33.3264432","reason":"i don't know","prevDayCount":0,"satisfied":false}""".stripMargin
//  println(decode[FailLine](json))
//
//}
