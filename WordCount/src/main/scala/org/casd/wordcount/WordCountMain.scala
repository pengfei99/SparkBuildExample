package org.casd.wordcount

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.{col, count}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}



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

  def countWord(data: RDD[(String)]):RDD[Row]  = {
    val result:RDD[(String, Int)] = data.flatMap(line => line.split(" ")).map(word => (word,1)).reduceByKey((a,b)=>a+b)
    val resInRow:RDD[Row] = result.map(t => Row(t._1,t._2))
    resInRow
  }
  // step1: create a spark session
  val spark= createSparkSession("Word count", isLocal = true)

  // step2: read data
  val sc = spark.sparkContext
  val filePath = "/home/pengfei/git/SparkBuildExample/data/file1.txt"
  val data = sc.textFile(filePath)

  // step3: get rdd
  val countInRdd = countWord(data)

  // step4: create dataframe
  val schema = new StructType().add(StructField("Word",StringType,nullable = false)).add(StructField("Word",IntegerType,nullable = false))
  val df = spark.createDataFrame(countInRdd, schema)
  df.show(10)
}

