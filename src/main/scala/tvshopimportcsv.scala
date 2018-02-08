import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.SaveMode
import scala.io.Source
import grizzled.slf4j.Logger
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import java.io.InputStream
import org.apache.spark.sql.SQLContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.sql.Timestamp
import java.io._
import sys.process._

object TvShopImportCsv extends Serializable { 

    lazy val logger = Logger(getClass())

    def main(args: Array[String]) {

        // this is for the table partition
        val currentTime = System.currentTimeMillis()
        val format = new SimpleDateFormat("yyyy-MM-dd")
        val dt_dia = format.format(currentTime)

        //
        // TABLES
        //
        val blocks_table_name = "datalake_tvshoptime.tvst_blocos"
        val blocks_table_create_query_file = "create-BLOCOS_TABLE.sql"
        val stream1 : InputStream = getClass.getResourceAsStream(blocks_table_create_query_file)
        val blocks_table_create_query = scala.io.Source.fromInputStream(stream1).getLines().mkString

        val timeline_table_name = "datalake_tvshoptime.tvst_timeline"
        val timeline_table_create_query_file = "create-TIMELINE_TABLE.sql"
        val stream2 : InputStream = getClass.getResourceAsStream(timeline_table_create_query_file)
        val timeline_table_create_query = scala.io.Source.fromInputStream(stream2).getLines().mkString

        //
        // INIT
        //

        val currentDate = "date " + "+%m%d%y%H%M" !!;

        logger.info("LOGGER : starting initialization...")

       
        val conf = new SparkConf().setAppName("TvShoptime Import Csv Application")
        val sc = new SparkContext(conf)

        val sqlContext = new org.apache.spark.sql.hive.HiveContext(sc)

        import sqlContext.implicits._

        sqlContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")


        sqlContext.sql(blocks_table_create_query)
        sqlContext.sql(timeline_table_create_query)

        logger.info("LOGGER : completed initialization...") 

        //
        // INPUT FILES PREPARATION
        //

        logger.info("LOGGER : starting processing...")

        loadCsvAndPersist(sqlContext, "upload/blocks/", "fail/blocks", dt_dia, blocks_table_name)
        loadCsvAndPersist(sqlContext, "upload/timeline/", "fail/timeline", dt_dia, timeline_table_name)

        logger.info("LOGGER: finished processing files...")
    }

    def loadCsvAndPersist(sqlContext: HiveContext, fileDir: String, failDir: String, dtDia: String, destTableName: String) = {
        try {

            //
            // CSV IMPORT
            //
            import org.apache.spark.sql.functions.lit
        
            val df = sqlContext.read.format("com.databricks.spark.csv")
              .option("delimiter", ";")
              .option("header", "true")
              .option("inferSchema", "true")
              .option("mode", "DROPMALFORMED")
              .load("hdfs:///" + "/user/tvshoptime/" + fileDir + "*.csv")

            //
            // DATA PREPARATION
            //

            logger.info("LOGGER : starting data preparation...")

            val preparedDf = df.na.drop()
              .withColumn("dt_dia",lit(dtDia))

            logger.info("LOGGER : finished data preparation...")

            //
            // INSERTS INTO DATABASE
            //

            logger.info("LOGGER : starting data insert...")

            preparedDf.write.partitionBy("dt_dia").mode(SaveMode.Append).saveAsTable(destTableName)
            logger.info("LOGGER : finished data insert...")

            //
            // FINISH with a clean-up
            //
            "hdfs dfs -rm -f " + "/user/tvshoptime/" + fileDir + "/*.csv " !;
            logger.info("LOGGER : completed csv processing...")

        } catch {
            case e: Exception => {

                //
                // FINISH with a move to fail
                //
                "hdfs dfs -mv " + "/user/tvshoptime/" + fileDir + "/*.csv " + "/user/tvshoptime/" + failDir !;
                logger.error("LOGGER : ERROR in files processing..." + e.toString, e)
            }
        }
    }
}
