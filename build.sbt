lazy val root = project
  .in(file("."))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    inThisBuild(List(
      organization := "pl.tues",
      version      := "0.1-SNAPSHOT",
      scalaVersion := "2.11.11"
    )),
    name := "Binding.scala Deposit Calculator",
    libraryDependencies ++= Seq(
      "org.scala-js"             %%% "scalajs-dom" % "0.9.2",
      "com.thoughtworks.binding" %%% "binding"     % "10.0.2",
      "com.thoughtworks.binding" %%% "dom"         % "10.0.2"
    ),
    addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
    ),
    scalaJSUseMainModuleInitializer := true
  )


// Automatically generate index-dev.html which uses *-fastopt.js
resourceGenerators in Compile += Def.task {
  val source = (resourceDirectory in Compile).value / "index.html"
  val target = (resourceManaged in Compile).value / "index-dev.html"

  val fullFileName = (artifactPath in (Compile, fullOptJS)).value.getName
  val fastFileName = (artifactPath in (Compile, fastOptJS)).value.getName

  IO.writeLines(target,
    IO.readLines(source).map {
      line => line.replace(fullFileName, fastFileName)
    }
  )

  Seq(target)
}.taskValue
