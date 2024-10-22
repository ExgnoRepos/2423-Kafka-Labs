package x.spark_streaming;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;

public class WordCount2_Kafka {

	public static void main(String[] args) throws Exception {
		
		// set a default master if none is set
		SparkConf conf = new SparkConf();
		if (!conf.contains("spark.master")) {
			System.out.println("Setting default master to : local[*]");
			//conf.set("spark.master", "local[*]");
			conf.setMaster("local[*]");
		}

		SparkSession spark = SparkSession.builder().
				appName("WordCount2Kafka")
				.config(conf)
				//.master("local[*]")  // will be set by spark-submit
				.getOrCreate();

		spark.sparkContext().setLogLevel("ERROR");

		Dataset<Row> df = spark.readStream()
							   .format("kafka")
							   .option("kafka.bootstrap.servers", "localhost:9092,kafka1:19092")
							   .option("subscribe", "test").load();
		
		df.printSchema();
		
		// print out raw data
		StreamingQuery query = df.writeStream()
		  .outputMode("append")
		  .format("console")
		  .start();
		
		/*-
		// TODO-1 : Convert raw bytes to string
		
		Dataset <Row> df2  = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)");
		df2.printSchema();
		
		StreamingQuery query = df2.writeStream()
				  .outputMode("append")
				  .format("console")
				  .start();
		*/
		
		/*-
		 
		// TODO-2: extract value
		Dataset <String> df3 = df2.select("value").as(Encoders.STRING());
		df3.printSchema();
		
		// print out incoming data
		StreamingQuery query = df3.writeStream()
				  .outputMode("append")
				  .format("console")
				  .start();
 
 */
		
		/*-
		// TODO-3: break lines into words
		
		Dataset<String> words = df3.flatMap((FlatMapFunction<String, String>) s -> {
			return  Arrays.asList(s.toLowerCase().split("\\s+")).iterator();
		},
				Encoders.STRING());
		
		StreamingQuery query = words.writeStream()
				  .outputMode("append")
				  .format("console")
				  .start();
		 */
		
		/*-
		// TODO-4: Generate running word count
		Dataset<Row> wordCounts = words.groupBy("value").count();
		
		// Start running the query that prints the running counts to the console
		StreamingQuery query = wordCounts.writeStream()
				 				 .outputMode("complete")
				                 .format("console")
				                 .start();
		 */

		query.awaitTermination();

		spark.stop();
	}

}
