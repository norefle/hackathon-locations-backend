organization  := "com.hackathon"

version       := "0.1.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
    "Spray repo" at "http://repo.spray.io/",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= {
    val akkaV = "2.3.9"
    val sprayV = "1.3.3"
    Seq(
        "io.spray"                  %%  "spray-can"     % sprayV,
        "io.spray"                  %%  "spray-routing" % sprayV,
        "io.spray"                  %%  "spray-json"    % "1.3.0",
        "io.spray"                  %%  "spray-testkit" % sprayV  % "test",
        "com.typesafe.akka"         %%  "akka-actor"    % akkaV,
        "com.typesafe.akka"         %%  "akka-testkit"  % akkaV   % "test",
        "org.specs2"                %%  "specs2-core"   % "2.3.11" % "test",
        "org.reactivemongo"         %%  "reactivemongo" % "0.11.4",
        "com.github.nscala-time"    %%  "nscala-time"   % "2.0.0"
    )
}

assemblyJarName := "hazardous-0.1.0.jar"

Revolver.settings
