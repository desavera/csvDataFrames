import sbtassembly.AssemblyPlugin.autoImport._

name := "tvshoptime-data-ingestion" 
organization := "mario.vera@b2wdigital.com"
version := "0.5"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                  Resolver.bintrayRepo("hseeberger", "maven"))

assemblyMergeStrategy in assembly := {

    case "META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat"  => MergeStrategy.first
    case "kryo*" => MergeStrategy.discard
    case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.first
    case PathList("org", "apache", xs @ _*) => MergeStrategy.first
    case PathList("com", "google", xs @ _*) => MergeStrategy.first
    case "overview.html" => MergeStrategy.first
    case "parquet.thrift" => MergeStrategy.first
    case "plugin.xml" => MergeStrategy.first
    case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

libraryDependencies ++= Seq(
    "org.clapper" %% "grizzled-slf4j" % "1.0.1",
    "com.databricks" %% "spark-csv" % "1.2.0",
    "org.apache.spark" %% "spark-core" % "1.6.0" % "provided",
    "org.apache.spark" %% "spark-hive" % "1.6.1" % "provided",
    "org.apache.spark" %% "spark-sql" % "1.6.1" % "provided")

