package volk.stopit.util

import cats.effect._
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Config {

  case class AppConfig(
      fails: StorageConfig,
      appName: Option[String],
      port: Option[String]
//      notes: StorageConfig // tbi
  )

  case class StorageConfig(
      path: Option[String],
      dateTimeFormat: Option[String]
  )

  def cfg: IO[AppConfig] = IO {
    val possibleConfigs = List(
      Option(System.getProperty("config")),
      Some(Utils.getJarDir + "config.conf")
    ).flatten
      .flatMap(Utils.getFile)

    possibleConfigs
      .foldLeft(ConfigSource.empty) {
        case (cfg, file) => cfg.withFallback(ConfigSource.file(file))
      }
      .withFallback(ConfigSource.defaultApplication)
      .loadOrThrow[AppConfig]

  }

}
