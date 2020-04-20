import ReleaseTransformations._


ThisBuild / scalaVersion := "2.12.7"

ThisBuild / organization := "com.example"

lazy val hello = (project in file("."))
  .settings(
    name := "Hello",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.9",
    libraryDependencies += "com.eed3si9n" %% "gigahorse-okhttp" % "0.3.1",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    releaseIgnoreUntrackedFiles := true,
  )
publish / skip := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runClean,                               // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
//  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)
releaseIgnoreUntrackedFiles := true

commands += Command.command("prepareRelease")((state: State) => {
  println("Preparing release...")
  val extracted = Project extract state
  var st = extracted.append(Seq(releaseProcess := Seq[ReleaseStep](
    runClean,
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
  )), state)
  Command.process("release with-defaults", st)
})

commands += Command.command("completeRelease")((state: State) => {
  println("Completing release...")
  val extracted = Project extract state
  val customState = extracted.append(Seq(releaseProcess := Seq[ReleaseStep](
    inquireVersions,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )), state)
  val newState = Command.process("release with-defaults", customState)
  newState
})
