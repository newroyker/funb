package roy

import org.apache.spark.sql.functions.{col, udf}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

object Common {
  val FORMAT = "delta"

  def save(df: DataFrame, path: String): Unit =
    df.write.format(FORMAT).mode(SaveMode.Overwrite).save(path)

  def load[T](spark: SparkSession, path: String): DataFrame =
    spark.read.format(FORMAT).load(path)

  def fib(n: Long): Long =
    if (n == 0L) 0L
    else if (n == 1L) 1L
    else (2L to n).foldLeft((0L, 1L)) { case ((fi_2, fi_1), _) => (fi_1, fi_1 + fi_2) }._2

  val fibUdf = udf((n: Long) => fib(n))

  def fibXform(df: DataFrame, colName: String): DataFrame =
    df.withColumn(colName, fibUdf(col(colName)))

  def hop(spark: SparkSession, source: String, sink: String, checkPoint: String, name: String): StreamingQuery =
    spark
      .readStream
      .format(FORMAT)
      .load(source)
      .writeStream
      .format(FORMAT)
      .outputMode("append")
      .option("checkpointLocation", checkPoint)
      .queryName(name)
      .start(sink)
}
