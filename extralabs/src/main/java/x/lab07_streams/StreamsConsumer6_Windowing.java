/// Filter out the streams
package x.lab07_streams;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.streams.kstream.TimeWindows;


import com.google.gson.Gson;
import java.time.Duration;

import x.utils.ClickstreamData;
import x.utils.MyConfig;

public class StreamsConsumer6_Windowing {
	private static final Logger logger = LoggerFactory.getLogger(StreamsConsumer6_Windowing.class);

	public static void main(String[] args) {

		Properties config = new Properties();
		// "bootstrap.servers" = "localhost:9092"
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.DEFAULT_BOOTSTRAP_SERVERS);
		config.put("group.id", "streaming6");
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-window");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		// config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
		// Serdes.Integer().getClass().getName());
		config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		// Records should be flushed every 10 seconds. This is less than the
		// default
		// in order to keep this example interactive.
		config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
		// For illustrative purposes we disable record caches
		config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

		final StreamsBuilder builder = new StreamsBuilder();

		 //  construct KStream
	    //  param 1 : topic name  : "clickstream"
	    final KStream<String, String> clickstream = builder.stream("clickstream");
		
		clickstream.print(Printed.toSysOut());

		// input::
		// key=domain,
		// value =
		// {"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}
		//
		// mapped output
		// key = action
		// value = 1
		final Gson gson = new Gson();
		final KStream<String, Integer> actionStream = clickstream
				.map(new KeyValueMapper<String, String, KeyValue<String, Integer>>() {
					public KeyValue<String, Integer> apply(String key, String value) {
						try {
							ClickstreamData clickstream = gson.fromJson(value, ClickstreamData.class);
							logger.debug("map() : got : " + value);
							String action = (clickstream.action != null) && (!clickstream.action.isEmpty())
									? clickstream.action
									: "unknown";
							KeyValue<String, Integer> actionKV = new KeyValue<>(action, 1);
							logger.debug("map() : returning : " + actionKV);
							return actionKV;
						} catch (Exception ex) {
							// logger.error("",ex);
							logger.error("Invalid JSON : \n" + value);
							return new KeyValue<String, Integer>("unknown", 1);
						}
					}
				});
		actionStream.print(Printed.toSysOut());

		// Now aggregate and count actions
		// we have to explicity state the K,V serdes in groupby, as the types are
		// changing

		//# TODO-1 : aggregate
		
		//#   param1 : key type :  Serdes.String()
		//#   param2 : value type :  Serdes.Integer()
		
		KGroupedStream<String, Integer> grouped = actionStream.groupByKey(
				Grouped.with(Serdes.String(), Serdes.Integer()));

		//TODO - 2
		//fix the windows interval and create the window frame TimeWindows.of(Duration.ofMinutes(1))

		//TimeWindowedKStream<String,Integer> windowedStream = grouped.windowedBy(??));

		
		//# TODO-3 : count windowed stream
		//# Hint : windowedstream.count()
		
		/*
		final KTable<Windowed<String>, Long> actionCount = windowedStream.???();
		actionCount.toStream().print(Printed.toSysOut());
		*/

		// start the stream
		final KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp();
		streams.start();

		logger.info("kstreams starting on " + MyConfig.TOPIC_CLICKSTREAM);

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
