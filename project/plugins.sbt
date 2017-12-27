
addSbtPlugin("org.scala-js"       % "sbt-scalajs"               % "1.0.0-M2"  )
addSbtPlugin("org.scala-js"       % "sbt-jsdependencies"        % "1.0.0-M2"  )
//addSbtPlugin("org.portable-scala" % "sbt-crossproject"          % "0.3.0"     )  // (1)
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"  % "0.3.0"     )  // (2)
//addSbtPlugin("org.scala-native"   % "sbt-scala-native"          % "0.3.3"     )  // (3)

libraryDependencies += "org.scala-js" %% "scalajs-env-nodejs" % "1.0.0-M2"
