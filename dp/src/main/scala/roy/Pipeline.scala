package roy

import org.apache.spark.sql.SparkSession

import scala.util.Try

import com.databricks.dbutils_v1.DBUtilsHolder.dbutils

object Pipeline {
  def main(args: Array[String]) {
    val spark = SparkSession.builder().getOrCreate()

    val bronzePath = "/tmp/roy/bronze"
    val silverPath = "/tmp/roy/silver"
    val goldPath = "/tmp/roy/gold"
    val checkpoint = "/tmp/roy/funbcp"

    dbutils.fs.rm(checkpoint, true)
    dbutils.fs.rm(bronzePath, true)
    dbutils.fs.rm(silverPath, true)
    dbutils.fs.rm(checkpoint, true)

    val bronze = spark.range(10).toDF()

    Common.save(bronze, bronzePath)

    val bronzeLoaded = Common.load(spark, bronzePath)

    bronzeLoaded.show(10)

    val silver = Common.fibXform(bronzeLoaded, "id")

    Common.save(silver, silverPath)

    val silverLoaded = Common.load(spark, silverPath)

    silverLoaded.show(10)

    val name = "dreamstream"

    val q = Common.hop(spark, silverPath, goldPath, checkpoint, name)

    Try{q.awaitTermination(10000)}

    val goldLoaded = Common.load(spark, goldPath)

    goldLoaded.show(10)

    spark.streams.active.filter(_.name == name).foreach(_.stop())

  }
}
