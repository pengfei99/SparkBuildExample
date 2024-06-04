package org.casd.wordcount

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.{col, count}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}

import scala.sys.exit



object WordCountMain extends App {
  /* This method create a spark session */
  def createSparkSession(appName: String, isLocal: Boolean): SparkSession = {
    if (isLocal) {
      SparkSession
        .builder()
        .config("spark.sql.caseSensitive", value = true)
        .config("spark.sql.session.timeZone", value = "UTC")
        .config("spark.driver.memory", value = "8G")
        .appName(appName)
        .master("local[*]")
        .getOrCreate()
    } else {
      SparkSession
        .builder()
        .config("spark.sql.caseSensitive", value = true)
        .config("spark.sql.session.timeZone", value = "UTC")
        .appName(appName)
        .getOrCreate()
    }
  }
 /* This method do the word count of a file*/
  def countWord(data: RDD[(String)]):RDD[Row]  = {
    val result:RDD[(String, Int)] = data.flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey((a,b)=>a+b)
    val resInRow:RDD[Row] = result.map(t => Row(t._1,t._2))
    resInRow
  }

  /* This method parse the command line arguments*/
  def parseArgs(args: Array[String]): (String, String) = {
    val usage = """
     Usage: spark-submit spark-wordcount.jar [--input-path path] [--output-path path]
     """
    if (args.isEmpty || args.length % 2 != 0) {
      println(usage)
      exit(1)
    }
    var inPath:String=""
    var outPath:String=""
    args.sliding(2, 2).toList.collect {
      case Array("--input-path", arg1: String) => inPath = arg1
      case Array("--output-path", arg2: String) => outPath = arg2
    }

    println(s"input path: ${inPath}")
    println(s"output path: ${outPath}")

    (inPath, outPath)
  }

  // step1: create a spark session, set local to true for testing, set it to False when building the jar
  val spark: SparkSession = createSparkSession("Word count", isLocal = true)

  // step2: parse argument
  val (inputPath, outputPath) = parseArgs(args = args)
  // val inputPath = "/home/pengfei/git/SparkBuildExample/data"
  // val outputPath = "/tmp"

  // step3: read data
  val sc: SparkContext = spark.sparkContext
  val filePath: String = s"${inputPath}/*.txt"
  val data: RDD[String] = sc.textFile(filePath)

  // step4: get word count rdd
  val countInRdd: RDD[Row] = countWord(data)

  // step5: create dataframe from the rdd
  val schema: StructType = new StructType().add(StructField("Word", StringType, nullable = false)).add(StructField("Count", IntegerType, nullable = false))
  val df: DataFrame = spark.createDataFrame(countInRdd, schema)
  df.show(10)

  // step6: write dataframe to output path
  val outfilePath = s"${outputPath}/wordCountRes"
  df.coalesce(1).write.mode("overwrite").csv(outfilePath)
}

