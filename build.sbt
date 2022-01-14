ThisBuild / version := "0.69"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "stopit",
    libraryDependencies ++= Seq(
      "com.github.pureconfig" %% "pureconfig"          % "0.17.1",
      "com.outr"              %% "scribe-slf4j"        % "3.6.8",
      "org.http4s"            %% "http4s-core"         % "1.0.0-M30",
      "org.http4s"            %% "http4s-dsl"          % "1.0.0-M30",
      "org.http4s"            %% "http4s-circe"        % "1.0.0-M30",
      "io.circe"              %% "circe-generic"       % "0.14.1",
      "org.http4s"            %% "http4s-ember-server" % "1.0.0-M30",
      "io.circe"              %% "circe-parser"        % "0.14.1"
    ),
    assembly / mainClass := Some("volk.stopit.Main"),
    assembly / assemblyJarName := "stopit.jar"
  )
