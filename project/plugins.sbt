resolvers ++= Seq(
  "retronym" at "http://retronym.github.com/repo/releases/"
)

addSbtPlugin("com.github.retronym" % "sbt-onejar"       % "0.8")

addSbtPlugin("com.typesafe.sbt"    % "sbt-start-script" % "0.10.0")
