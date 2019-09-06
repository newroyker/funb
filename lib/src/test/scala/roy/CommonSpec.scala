package roy

import java.io.File

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, MustMatchers}

import scala.util.Try

class CommonSpec extends FlatSpec with MustMatchers with BeforeAndAfterAll{

  val ns: Seq[Long] = Seq(0L, 1L, 2L, 3L, 4L, 5L)
  val fns: Seq[Long] = Seq(0L, 1L, 1L, 2L, 3L, 5L)

  lazy val spark: SparkSession = SparkSession
    .builder
    .master("local[*]")
    .appName("CommonSpec")
    .config("spark.driver.host", "localhost")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")
  import spark.implicits._

  override def afterAll(): Unit = {
    super.afterAll()
    spark.streams.active.foreach(_.stop())
    spark.stop

    import scala.reflect.io.Directory
    val directory = new Directory(new File("data"))
    directory.deleteRecursively()
  }

  "Common" should "generate fibonacci given n" in {
    val tests: Map[Long, Long] = ns.zip(fns).toMap

    tests.foreach { case (k, v) =>
      Common.fib(k) must be(v)
    }
  }

  it should "generate fibonacci given DF" in {
    val input: DataFrame = ns.toDF()
    val expected: DataFrame = fns.toDF()
    val output = Common.fibXform(input, "value")

    output.collect must contain theSameElementsAs expected.collect
  }

  it should "save and load a DF" in {
    val expected: DataFrame = ns.toDF()
    Common.save(expected, "data/test")
    val output = Common.load(spark, "data/test")

    output.collect must contain theSameElementsAs expected.collect
  }

  it should "hop" in {
    val expected: DataFrame = ns.toDF()
    val silverPath = "data/silver"
    val goldPath = "data/gold"
    Common.save(expected, "data/silver")
    val q = Common.hop(spark, silverPath, goldPath, "data/checkpoint", "streamdream")
    Try{q.awaitTermination(10000)}
    val output = Common.load(spark, goldPath)

    output.collect must contain theSameElementsAs expected.collect
  }
}
