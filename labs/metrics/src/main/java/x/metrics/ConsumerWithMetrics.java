package x.metrics;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

import x.utils.MyConfig;
import x.utils.MyMetricsRegistry;
import x.utils.MyUtils;

public class ConsumerWithMetrics {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerWithMetrics.class);
	
	private static final Meter meterConsumerEvents = MyMetricsRegistry.metrics.meter("kafka-metrics1.consumer.events");
	
	private static long eventsReceived = 0;

	public static void main(String[] args) {

		Properties config = new Properties();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-metrics-consumer");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "metrics1");
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
			
		// process each record and report traffic
		clickstream.foreach(new ForeachAction<String, String>() {
			public void apply(String key, String value) {
				eventsReceived ++;
				logger.debug("received #" + eventsReceived + ",  key:" + key + ", value:" + value);

				meterConsumerEvents.mark();
				
				processEvent(key, value);
				// TODO-1: measure processing time
				// See solutions folder for hints
				
			}
		});

		// start the stream
		final KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp();
		streams.start();

		logger.info("kstreams starting on : clickstream " );

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}
	
	static void processEvent (String key, String value) {
		//Here we are just doing a random wait
		MyUtils.randomDelay(100, 800);
	}

}
