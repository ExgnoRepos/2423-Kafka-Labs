package x.lab09_metrics;

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

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;

import x.utils.MyConfig;
import x.utils.MyMetricsRegistry;

public class DomainTrafficReporter1 {
	private static final Logger logger = LoggerFactory.getLogger(DomainTrafficReporter1.class);
	
	private static long eventsReceived = 0;

	public static void main(String[] args) {

		Properties config = new Properties();
		// "bootstrap.servers" = "localhost:9092"
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.DEFAULT_BOOTSTRAP_SERVERS);
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "domain-traffic-reporter1");
//		config.put(ConsumerConfig.GROUP_ID_CONFIG, "traffic-reporter1");
		config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		// Records should be flushed every 10 seconds. This is less than the
		// default
		// in order to keep this example interactive.
		config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
		// For illustrative purposes we disable record caches
		config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);


		final StreamsBuilder builder = new StreamsBuilder();

		 final KStream<String, String> clickstream = builder.stream(MyConfig.TOPIC_CLICKSTREAM);
		// clickstream.print();

		// process each record and report traffic
		clickstream.foreach(new ForeachAction<String, String>() {
			public void apply(String key, String value) {
				eventsReceived++;
				logger.debug("Received event # " + eventsReceived + ", key:" + key + ", value:" + value);

			
				// since dots have special meaning in metrics, convert dots in
				// domain names into underscore
				// so facebook.com --> facebook_com
				String domain2 = key.replace(".", "_");

				//# TODO-1 : mark 'traffic2.total'
				// Meter meterTotalTraffic = MyMetricsRegistry.metrics.meter("???");
				// meterTotalTraffic.???

				//# TODO-2 report metrics
				//#    - use 'MyMetricsRegistry.metrics.counter()  API to get a counter
				//#    - for the name of the counter use  : traffic + domain2
				//#    - call 'inc' method on counter  (counter.inc())
				
				// Counter counter = MyMetricsRegistry.metrics.???("????");
				// counter.???

				//# TODO-3 :  report metrics2
				//#   - get a meter with name 'traffic2 + domain2'
				//#   - call 'mark' method on meter
				// Meter meter = MyMetricsRegistry.metrics.???("???");
				// meter.???

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
