/// Filter out the streams
package x.kafka_streams;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import x.MyConfig;
import x.utils.ClickstreamData;

public class StreamingConsumer3_Filter {
	private static final Logger logger = LoggerFactory.getLogger(StreamingConsumer3_Filter.class);

	public static void main(String[] args) {

		Properties config = new Properties();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		config.put("group.id", "kafka-streams-filter");
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-filter");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		// Records should be flushed every 10 seconds. This is less than the
		// default
		// in order to keep this example interactive.
		config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
		// For illustrative purposes we disable record caches
		config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

		final StreamsBuilder builder = new StreamsBuilder();

		final KStream<String, String> clickstream = builder.stream("clickstream");
		// clickstream.print(Printed.toSysOut());

		final Gson gson = new Gson();
		// filter clicks only
		final KStream<String, String> actionClickedStream = clickstream.filter((key, value) -> {
			// quick fix
			// return value.contains("clicked");

			//// more robust solution: parse JSON data
			try {
				ClickstreamData clickstreamData = gson.fromJson(value, ClickstreamData.class);
				return ((clickstreamData.action != null) && (clickstreamData.action.equals("clicked")));

			} catch (Exception e) {
				return false;
			}

		});
		
		actionClickedStream.print(Printed.toSysOut());

		// start the stream
		final KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp();
		streams.start();

		logger.info("kstreams starting on clickstream");

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
