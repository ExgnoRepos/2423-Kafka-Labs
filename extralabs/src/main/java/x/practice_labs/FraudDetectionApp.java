package x.practice_labs;

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

import x.utils.ClickstreamData;
import x.utils.MyConfig;


public class FraudDetectionApp {
        
        private static final Logger logger = LoggerFactory.getLogger(FraudDetectionApp.class);
        
        public static void main(String[] args) {
                Properties config = new Properties();
                config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.DEFAULT_BOOTSTRAP_SERVERS);
                config.put("group.id", "fraud-detection1");
                config.put(StreamsConfig.APPLICATION_ID_CONFIG, "fraud-detection1");
                config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
                config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
                config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
                // Records should be flushed every 10 seconds. This is less than the
                // default
                // in order to keep this example interactive.
                config.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10 * 1000);
                // For illustrative purposes we disable record caches
                config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
                
                FraudIPLookup fraudIPLookup = new CacheIPLookup(); // our default cache
                
                final StreamsBuilder builder = new StreamsBuilder();
                
                final KStream<String, String> clickstream = builder.stream("clickstream");
                
                // print to console
                // clickstream.print(Printed.toSysOut());
                
                // clickstream data will arrive in this format:
                // key : twitter.com
                // value: {"timestamp": 1642840429496, "ip": "4.4.8.2", "user": "user-70", 
                //          "action": "clicked", "domain": "twitter.com", 
                //          "campaign": "campaign-99", "cost": 26}
                
                //# TODO-1 : filter fraudIPs
                Gson gson = new Gson();
                final KStream<String, String> fraudStream = clickstream
                .filter((key, value) -> {
                        try {
                                // here 'value' is JSON string
                                // # TODO-1a: Extract the ClickstreamData from JSON String.  Use 'json.fromJson ()'
                                // # Look at previous labs for sample code
                                // ClickstreamData clickstreamData =
                                // gson.???(???, ClickstreamData.class);

                                // TODO-1b: Extract the ip attribute from clickstreamData
                                String ip = "???";  
                                
                                return fraudIPLookup.isFraudIP(ip);
                        } catch (Exception e) {
                                return false;
                        }
                });
                
                fraudStream.print(Printed.toSysOut());
                
                
                // TODO-2 - BONUS lab:
                // Send the fraudIPs to another topic
                // See StreamingConsumer5_GroupBy.java  for example
                
                // start the stream
                final KafkaStreams streams = new KafkaStreams(builder.build(), config);
                streams.cleanUp();
                streams.start();
                
                logger.info("kstreams starting on " + MyConfig.TOPIC_CLICKSTREAM);
                
                Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        }
        
}
