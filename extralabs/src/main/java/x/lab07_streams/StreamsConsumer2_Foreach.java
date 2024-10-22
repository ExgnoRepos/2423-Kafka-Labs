/// Simple Streaming Consumer
package x.lab07_streams;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.utils.MyConfig;

public class StreamsConsumer2_Foreach {
	private static final Logger logger = LoggerFactory.getLogger(StreamsConsumer2_Foreach.class);

	public static void main(String[] args) {

		Properties config = new Properties();
		// "bootstrap.servers" = "localhost:9092"
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.DEFAULT_BOOTSTRAP_SERVERS);
		config.put("group.id", "streaming2");
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-foreach");
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

		// print to console
		// clickstream.print(Printed.toSysOut());

		// process events one by one
		clickstream.foreach(new ForeachAction<String, String>() {
			long counter = 0;
			public void apply(String key, String value) {
				counter ++;
				//# TODO-1 : print out the record (key and value)
				//logger.debug("FOREACH [" + counter + "]:: KEY:" + ??? + ", VALUE:" + ??? + "\n");
			}
		});

		// start the stream
		final KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp();
		streams.start();
		logger.info("kstreams starting on " + MyConfig.TOPIC_CLICKSTREAM);

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
