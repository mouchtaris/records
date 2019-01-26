addSbtPlugin("org.scala-js"       % "sbt-scalajs"               % "1.0.0-M6"  )
addSbtPlugin("org.scala-js"       % "sbt-jsdependencies"        % "1.0.0-M6"  )
//addSbtPlugin("org.portable-scala" % "sbt-crossproject"          % "0.3.0"     )  // (1)
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"  % "0.6.0"     )  // (2)
//addSbtPlugin("org.scala-native"   % "sbt-scala-native"          % "0.3.3"     )  // (3)

libraryDependencies += "org.scala-js" %% "scalajs-env-nodejs" % "1.0.0-M6"
libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.0.0-M6"


addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.18")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.7.4"
