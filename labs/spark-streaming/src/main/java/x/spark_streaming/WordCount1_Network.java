package x.spark_streaming;

/*
 $	spark-submit   --class  x.spark_streaming.NetworkWordCount  target/spark-streaming-1.0.jar
 */

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;

public class WordCount1_Network {

	public static void main(String[] args) throws Exception {
		SparkSession spark = SparkSession.builder().
							 appName("WordCount1Network").
							 master("local[*]").
							 getOrCreate();
		spark.sparkContext().setLogLevel("ERROR");

		Dataset<Row> lines = spark.readStream()
							      .format("socket")
							      .option("host", "localhost")
							      .option("port", 10000)
				                  .load();
		
		// print out incoming data
		StreamingQuery query = lines.writeStream()
		  .outputMode("append")
		  .format("console")
		  .start();

		/*-
		//TODO-1: Split the lines into words
		Dataset<String> words = lines.as(Encoders.STRING())
									 .flatMap((FlatMapFunction<String, String>) x -> 
									 Arrays.asList(x.split("\\s+")).iterator(), 
									 Encoders.STRING());
		
		// TODO-2: You will notice the word count is case sensitive.
		// so 'a' and 'A' are treated as different words
		// fix the line above so words are treated case-insensitive
		// Hint :  Arrays.asList(x.toLowerCase().split("\\s+")).iterator(),
		 
		// print out words data
		StreamingQuery query = words.writeStream()
		  .outputMode("append")
		  .format("console")
		  .start();

		*/
		
		/*- 
                //# TODO-3: do word-count

		// Generate running word count
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
