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
import com.google.gson.Gson;

import x.utils.ClickstreamData;
import x.utils.MyConfig;
import x.utils.MyMetricsRegistry;

public class DomainTrafficReporter1 {
	private static final Logger logger = LoggerFactory.getLogger(DomainTrafficReporter1.class);

	private static long eventsReceived = 0;

	public static void main(String[] args) {

		Properties config = new Properties();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "domain-traffic-reporter1");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "domain-traffic-reporter1");
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
		
		final Gson gson = new Gson();

		// process each record and report traffic
		clickstream.foreach(new ForeachAction<String, String>() {
			public void apply(String key, String value) {
				eventsReceived++;
				logger.debug("Received event # " + eventsReceived + ", key:" + key + ", value:" + value);

				// ==== report domain based metrics
				Meter meterTotalTraffic = MyMetricsRegistry.metrics.meter("kafka-metrics2.consumer.traffic.total");
				meterTotalTraffic.mark();

				// since dots have special meaning in metrics, convert dots in
				// domain names into underscore
				// so facebook.com --> facebook_com
				String domain2 = key.replace(".", "_");

				// report metrics per domain
				Meter meterTrafficPerDomain = MyMetricsRegistry.metrics
						.meter("kafka-metrics2.consumer.traffic." + domain2);
				meterTrafficPerDomain.mark();

				// ===== TODO: also report action stats ====
				try {
					ClickstreamData clickstreamData = gson.fromJson(value, ClickstreamData.class);
					// TODO: extract action from clickstreamData
					// TODO: Obtain a meter for each action.  See sample code above
					// TODO: Mark the meter
				} catch (Exception e) {
					//  ignore
				}

			}
		});

		// start the stream
		final KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp();
		streams.start();

		logger.info("kstreams starting on clickstream");

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
