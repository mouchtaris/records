@ECHO OFF
IF NOT "%~f0" == "~f0" GOTO :WinNT
@"java -cp ;C:\Users\rudolf\IdeaProjects\rapture\rapture\jvm\target\scala-2.12\classes;C:\Users\rudolf\.ivy2\cache\org.jruby.joni\joni\jars\joni-2.1.15.jar;C:\Users\rudolf\.ivy2\cache\org.jruby.jcodings\jcodings\jars\jcodings-1.0.27.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-posix\jars\jnr-posix-3.0.44.jar;C:\Users\rudolf\.ivy2\cache\com.lihaoyi\sourcecode_2.12\bundles\sourcecode_2.12-0.1.4.jar;C:\Users\rudolf\.ivy2\cache\com.lihaoyi\fastparse_2.12\jars\fastparse_2.12-1.0.0.jar;C:\Users\rudolf\.ivy2\cache\com.lihaoyi\fastparse-utils_2.12\jars\fastparse-utils_2.12-1.0.0.jar;C:\Users\rudolf\.ivy2\cache\org.scala-lang.modules\scala-parser-combinators_2.12\bundles\scala-parser-combinators_2.12-1.0.4.jar;C:\Users\rudolf\.ivy2\cache\org.reactivestreams\reactive-streams\jars\reactive-streams-1.0.1.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-stream_2.12\jars\akka-stream_2.12-2.5.8.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-parsing_2.12\jars\akka-parsing_2.12-10.0.11.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-http_2.12\jars\akka-http_2.12-10.0.11.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-http-core_2.12\jars\akka-http-core_2.12-10.0.11.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe\ssl-config-core_2.12\bundles\ssl-config-core_2.12-0.2.2.jar;C:\Users\rudolf\.ivy2\cache\xpp3\xpp3\jars\xpp3-1.1.4c.jar;C:\Users\rudolf\.ivy2\cache\org.apache.httpcomponents\httpcore\jars\httpcore-4.0.1.jar;C:\Users\rudolf\.ivy2\cache\org.apache.httpcomponents\httpclient\jars\httpclient-4.0.1.jar;C:\Users\rudolf\.ivy2\cache\javax.transaction\transaction-api\jars\transaction-api-1.1.jar;C:\Users\rudolf\.ivy2\cache\javax.jdo\jdo2-api\jars\jdo2-api-2.3-eb.jar;C:\Users\rudolf\.ivy2\cache\commons-logging\commons-logging\jars\commons-logging-1.1.1.jar;C:\Users\rudolf\.ivy2\cache\commons-codec\commons-codec\jars\commons-codec-1.3.jar;C:\Users\rudolf\.ivy2\cache\com.google.oauth-client\google-oauth-client-java6\jars\google-oauth-client-java6-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.oauth-client\google-oauth-client\jars\google-oauth-client-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-xml\jars\google-http-client-xml-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-protobuf\jars\google-http-client-protobuf-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-jdo\jars\google-http-client-jdo-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-jackson2\jars\google-http-client-jackson2-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-gson\jars\google-http-client-gson-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client\jars\google-http-client-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.guava\guava-jdk5\bundles\guava-jdk5-17.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.code.gson\gson\jars\gson-2.1.jar;C:\Users\rudolf\.ivy2\cache\com.google.code.findbugs\jsr305\jars\jsr305-1.3.9.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-xml\jars\google-api-client-xml-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-protobuf\jars\google-api-client-protobuf-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-java6\jars\google-api-client-java6-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-jackson2\jars\google-api-client-jackson2-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-gson\jars\google-api-client-gson-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client\jars\google-api-client-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.fasterxml.jackson.core\jackson-core\jars\jackson-core-2.1.3.jar;C:\Users\rudolf\.ivy2\cache\org.scala-lang\scala-reflect\jars\scala-reflect-2.12.4.jar;C:\Users\rudolf\.ivy2\cache\org.scala-lang\scala-library\jars\scala-library-2.12.4.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe\config\bundles\config-1.3.2.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-actor_2.12\jars\akka-actor_2.12-2.5.8.jar;C:\Users\rudolf\.ivy2\cache\org.scala-lang.modules\scala-java8-compat_2.12\bundles\scala-java8-compat_2.12-0.8.0.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jffi\jars\jffi-1.2.16.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jffi\jars\jffi-1.2.16-native.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-constants\jars\jnr-constants-0.9.9.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-enxio\jars\jnr-enxio-0.16.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-netdb\jars\jnr-netdb-1.1.6.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-unixsocket\jars\jnr-unixsocket-0.17.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-x86asm\jars\jnr-x86asm-1.0.2.jar;C:\Users\rudolf\.ivy2\cache\com.headius\invokebinder\bundles\invokebinder-1.10.jar;C:\Users\rudolf\.ivy2\cache\com.headius\modulator\jars\modulator-1.0.jar;C:\Users\rudolf\.ivy2\cache\com.headius\options\jars\options-1.4.jar;C:\Users\rudolf\.ivy2\cache\com.headius\unsafe-fences\jars\unsafe-fences-1.0.jar;C:\Users\rudolf\.ivy2\cache\com.jcraft\jzlib\jars\jzlib-1.1.3.jar;C:\Users\rudolf\.ivy2\cache\com.martiansoftware\nailgun-server\jars\nailgun-server-0.9.1.jar;C:\Users\rudolf\.ivy2\cache\joda-time\joda-time\jars\joda-time-2.8.2.jar;C:\Users\rudolf\.ivy2\cache\org.jruby\dirgra\jars\dirgra-0.3.jar;C:\Users\rudolf\.ivy2\cache\org.jruby.extras\bytelist\jars\bytelist-1.0.15.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.slick\slick-codegen_2.12\jars\slick-codegen_2.12-3.2.1.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.slick\slick-hikaricp_2.12\bundles\slick-hikaricp_2.12-3.2.1.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.slick\slick_2.12\bundles\slick_2.12-3.2.1.jar;C:\Users\rudolf\.ivy2\cache\com.zaxxer\HikariCP\bundles\HikariCP-2.5.1.jar;C:\Users\rudolf\.ivy2\cache\org.postgresql\postgresql\bundles\postgresql-42.1.4.jar;C:\Users\rudolf\.ivy2\cache\org.slf4j\slf4j-api\jars\slf4j-api-1.7.21.jar;C:\Users\rudolf\.ivy2\cache\org.slf4j\slf4j-nop\jars\slf4j-nop-1.6.4.jar;C:\Users\rudolf\.ivy2\cache\com.google.protobuf\protobuf-java\bundles\protobuf-java-3.5.1.jar;C:\Users\rudolf\.ivy2\cache\com.thesamet.scalapb\lenses_2.12\jars\lenses_2.12-0.7.0.jar;C:\Users\rudolf\.ivy2\cache\com.thesamet.scalapb\scalapb-runtime_2.12\jars\scalapb-runtime_2.12-0.7.4.jar;C:\Users\rudolf\.ivy2\cache\org.jruby\jruby\jars\jruby-9.1.17.0.jar;C:\Users\rudolf\.ivy2\cache\org.jruby\jruby-core\jars\jruby-core-9.1.17.0.jar;C:\Users\rudolf\.ivy2\cache\org.jruby\jruby-stdlib\jars\jruby-stdlib-9.1.17.0.jar org.jruby.Main" "C:/Users/rudolf/IdeaProjects/rapture/rapture/jvm/src/main/resources/_rubyjams/bin/bundler" %1 %2 %3 %4 %5 %6 %7 %8 %9
GOTO :EOF
:WinNT
@"java -cp ;C:\Users\rudolf\IdeaProjects\rapture\rapture\jvm\target\scala-2.12\classes;C:\Users\rudolf\.ivy2\cache\org.jruby.joni\joni\jars\joni-2.1.15.jar;C:\Users\rudolf\.ivy2\cache\org.jruby.jcodings\jcodings\jars\jcodings-1.0.27.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-posix\jars\jnr-posix-3.0.44.jar;C:\Users\rudolf\.ivy2\cache\com.lihaoyi\sourcecode_2.12\bundles\sourcecode_2.12-0.1.4.jar;C:\Users\rudolf\.ivy2\cache\com.lihaoyi\fastparse_2.12\jars\fastparse_2.12-1.0.0.jar;C:\Users\rudolf\.ivy2\cache\com.lihaoyi\fastparse-utils_2.12\jars\fastparse-utils_2.12-1.0.0.jar;C:\Users\rudolf\.ivy2\cache\org.scala-lang.modules\scala-parser-combinators_2.12\bundles\scala-parser-combinators_2.12-1.0.4.jar;C:\Users\rudolf\.ivy2\cache\org.reactivestreams\reactive-streams\jars\reactive-streams-1.0.1.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-stream_2.12\jars\akka-stream_2.12-2.5.8.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-parsing_2.12\jars\akka-parsing_2.12-10.0.11.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-http_2.12\jars\akka-http_2.12-10.0.11.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-http-core_2.12\jars\akka-http-core_2.12-10.0.11.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe\ssl-config-core_2.12\bundles\ssl-config-core_2.12-0.2.2.jar;C:\Users\rudolf\.ivy2\cache\xpp3\xpp3\jars\xpp3-1.1.4c.jar;C:\Users\rudolf\.ivy2\cache\org.apache.httpcomponents\httpcore\jars\httpcore-4.0.1.jar;C:\Users\rudolf\.ivy2\cache\org.apache.httpcomponents\httpclient\jars\httpclient-4.0.1.jar;C:\Users\rudolf\.ivy2\cache\javax.transaction\transaction-api\jars\transaction-api-1.1.jar;C:\Users\rudolf\.ivy2\cache\javax.jdo\jdo2-api\jars\jdo2-api-2.3-eb.jar;C:\Users\rudolf\.ivy2\cache\commons-logging\commons-logging\jars\commons-logging-1.1.1.jar;C:\Users\rudolf\.ivy2\cache\commons-codec\commons-codec\jars\commons-codec-1.3.jar;C:\Users\rudolf\.ivy2\cache\com.google.oauth-client\google-oauth-client-java6\jars\google-oauth-client-java6-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.oauth-client\google-oauth-client\jars\google-oauth-client-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-xml\jars\google-http-client-xml-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-protobuf\jars\google-http-client-protobuf-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-jdo\jars\google-http-client-jdo-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-jackson2\jars\google-http-client-jackson2-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client-gson\jars\google-http-client-gson-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.http-client\google-http-client\jars\google-http-client-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.guava\guava-jdk5\bundles\guava-jdk5-17.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.code.gson\gson\jars\gson-2.1.jar;C:\Users\rudolf\.ivy2\cache\com.google.code.findbugs\jsr305\jars\jsr305-1.3.9.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-xml\jars\google-api-client-xml-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-protobuf\jars\google-api-client-protobuf-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-java6\jars\google-api-client-java6-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-jackson2\jars\google-api-client-jackson2-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client-gson\jars\google-api-client-gson-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.google.api-client\google-api-client\jars\google-api-client-1.23.0.jar;C:\Users\rudolf\.ivy2\cache\com.fasterxml.jackson.core\jackson-core\jars\jackson-core-2.1.3.jar;C:\Users\rudolf\.ivy2\cache\org.scala-lang\scala-reflect\jars\scala-reflect-2.12.4.jar;C:\Users\rudolf\.ivy2\cache\org.scala-lang\scala-library\jars\scala-library-2.12.4.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe\config\bundles\config-1.3.2.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.akka\akka-actor_2.12\jars\akka-actor_2.12-2.5.8.jar;C:\Users\rudolf\.ivy2\cache\org.scala-lang.modules\scala-java8-compat_2.12\bundles\scala-java8-compat_2.12-0.8.0.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jffi\jars\jffi-1.2.16.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jffi\jars\jffi-1.2.16-native.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-constants\jars\jnr-constants-0.9.9.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-enxio\jars\jnr-enxio-0.16.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-netdb\jars\jnr-netdb-1.1.6.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-unixsocket\jars\jnr-unixsocket-0.17.jar;C:\Users\rudolf\.ivy2\cache\com.github.jnr\jnr-x86asm\jars\jnr-x86asm-1.0.2.jar;C:\Users\rudolf\.ivy2\cache\com.headius\invokebinder\bundles\invokebinder-1.10.jar;C:\Users\rudolf\.ivy2\cache\com.headius\modulator\jars\modulator-1.0.jar;C:\Users\rudolf\.ivy2\cache\com.headius\options\jars\options-1.4.jar;C:\Users\rudolf\.ivy2\cache\com.headius\unsafe-fences\jars\unsafe-fences-1.0.jar;C:\Users\rudolf\.ivy2\cache\com.jcraft\jzlib\jars\jzlib-1.1.3.jar;C:\Users\rudolf\.ivy2\cache\com.martiansoftware\nailgun-server\jars\nailgun-server-0.9.1.jar;C:\Users\rudolf\.ivy2\cache\joda-time\joda-time\jars\joda-time-2.8.2.jar;C:\Users\rudolf\.ivy2\cache\org.jruby\dirgra\jars\dirgra-0.3.jar;C:\Users\rudolf\.ivy2\cache\org.jruby.extras\bytelist\jars\bytelist-1.0.15.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.slick\slick-codegen_2.12\jars\slick-codegen_2.12-3.2.1.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.slick\slick-hikaricp_2.12\bundles\slick-hikaricp_2.12-3.2.1.jar;C:\Users\rudolf\.ivy2\cache\com.typesafe.slick\slick_2.12\bundles\slick_2.12-3.2.1.jar;C:\Users\rudolf\.ivy2\cache\com.zaxxer\HikariCP\bundles\HikariCP-2.5.1.jar;C:\Users\rudolf\.ivy2\cache\org.postgresql\postgresql\bundles\postgresql-42.1.4.jar;C:\Users\rudolf\.ivy2\cache\org.slf4j\slf4j-api\jars\slf4j-api-1.7.21.jar;C:\Users\rudolf\.ivy2\cache\org.slf4j\slf4j-nop\jars\slf4j-nop-1.6.4.jar;C:\Users\rudolf\.ivy2\cache\com.google.protobuf\protobuf-java\bundles\protobuf-java-3.5.1.jar;C:\Users\rudolf\.ivy2\cache\com.thesamet.scalapb\lenses_2.12\jars\lenses_2.12-0.7.0.jar;C:\Users\rudolf\.ivy2\cache\com.thesamet.scalapb\scalapb-runtime_2.12\jars\scalapb-runtime_2.12-0.7.4.jar;C:\Users\rudolf\.ivy2\cache\org.jruby\jruby\jars\jruby-9.1.17.0.jar;C:\Users\rudolf\.ivy2\cache\org.jruby\jruby-core\jars\jruby-core-9.1.17.0.jar;C:\Users\rudolf\.ivy2\cache\org.jruby\jruby-stdlib\jars\jruby-stdlib-9.1.17.0.jar org.jruby.Main" "%~dpn0" %*
