package x.spark_streaming;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import x.utils.ClickstreamData;

public class FraudDetection1Network {

	private static final Logger logger = LoggerFactory.getLogger(FraudDetection1Network.class);
	
	private static Gson gson = new Gson();

	public static void main(String[] args) throws Exception {
		SparkSession spark = SparkSession.builder().
							 appName("FraudDetection1Network").
							 master("local[*]").
							 getOrCreate();
		spark.sparkContext().setLogLevel("ERROR");

		Dataset<Row> lines = spark.readStream()
							      .format("socket")
							      .option("host", "localhost")
							      .option("port", 10000)
				                  .load();

		Dataset <String> ips = lines.as(Encoders.STRING()).map((MapFunction<String, String>) x -> {
			try {
				logger.debug("got : " + x);
				ClickstreamData clickstreamData =
						gson.fromJson(x, ClickstreamData.class);
				return clickstreamData.ip;
			} catch (Exception e) {
				logger.debug("exception in parsing JSON :", e.getMessage());
				return "unknown";
			}
			
		}, Encoders.STRING());
		
		Dataset<String> fraudIPs = ips.filter((FilterFunction<String>)x -> {
			return false;
		});
		
		
		// Start running the query that prints the running counts to the console
		StreamingQuery query = ips.writeStream()
		  .outputMode("append")
		  .format("console")
		  .start();

		query.awaitTermination();
		
		spark.stop();
	}

}
