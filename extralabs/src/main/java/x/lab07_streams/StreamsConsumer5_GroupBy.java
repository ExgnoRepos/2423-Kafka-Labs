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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import x.utils.ClickstreamData;
import x.utils.MyConfig;

public class StreamsConsumer5_GroupBy {
	private static final Logger logger = LoggerFactory.getLogger(StreamsConsumer5_GroupBy.class);

	public static void main(String[] args) {

		Properties config = new Properties();
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.DEFAULT_BOOTSTRAP_SERVERS);
		config.put("group.id", "streaming5");
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-groupby");
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

		/*-
		 ==== First transformation is MAP ====
		 input::
			key=domain (String),
			value = JSON (String)
		            {"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}
		
		 mapped output:
		    key = action (String)
		    value = 1 (Integer) (used for counting / aggregating later)
		 */
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
		// actionStream.print(Printed.toSysOut());

		/*-
		 ==== Now aggregate and count actions ===
		we have to explicity state the K,V serdes in groupby, as the types are changing

		# TODO-1 : aggregate
			param1 : key type : Serdes.String()
			param2 : value type : Serdes.Integer()
		 groupByKey accepts grouped object with String serd as key and Integer serde as value
		 
		 uncomment the following 2 lines
		 */
		
		 // KGroupedStream<String, Integer> grouped =
		 // 					actionStream.groupByKey(Grouped.with(Serdes.String(), Serdes.Integer()));

		
		/*-
		  # TODO-2 : count grouped stream
		  Hint : grouped.count()
		  
		  uncomment the following 2 lines
		 */

		 // final KTable<String, Long> actionCount = grouped.count();
		 // actionCount.toStream().print(Printed.toSysOut());

		/*-
		 # BONUS lab 1 :
		 lets write the data into another topic
		 Hint : param 1 : name of queue : "action-count"
		 
		 And use 'consoleConsumer' to monitor the queue output!
		 
		 Here is a kafkacat example
		 	$   kafkacat -q -C -b localhost:9092 -t action-count  -s key=s  -s value=q -f '%k:%s\n'
		 */
		 
		// actionCount.toStream().to("???", Produced.with(Serdes.String(),Serdes.Long()));

		// start the stream
		final KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp();
		streams.start();

		logger.info("kstreams starting on " + MyConfig.TOPIC_CLICKSTREAM);

		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
