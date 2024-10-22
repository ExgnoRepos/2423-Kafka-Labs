package x.lab09_metrics;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.utils.MyConfig;
import x.utils.MyUtils;

public class DomainTrafficReporter2 {
	private static final Logger logger = LoggerFactory.getLogger(DomainTrafficReporter2.class);

	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		// "bootstrap.servers" = "localhost:9092"
	   config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.DEFAULT_BOOTSTRAP_SERVERS);
	   config.put(ConsumerConfig.GROUP_ID_CONFIG, "traffic-reporter2");
		config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("value.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
	
		KafkaConsumer<String, Long> consumer = new KafkaConsumer<>(config);
		consumer.subscribe(Arrays.asList(MyConfig.TOPIC_DOMAIN_COUNT)); // subscribe

		long eventsReceived = 0;
		boolean keepRunning = true;
		while (keepRunning) {
			ConsumerRecords<String, Long> records = consumer.poll(1000);
			if (records.count() > 0) {
				logger.debug("Got " + records.count() + " messages");
				for (ConsumerRecord<String, Long> record : records) {
					eventsReceived++;
					logger.debug("Received message # " + eventsReceived +  " : " + record);

					String domain = record.key();
					// since dots have special meaning in metrics, convert dots in domain names into underscore
					String domain2 = domain.replace(".", "_"); 
					long traffic = record.value();

					logger.debug(domain2 + " = " + traffic);

					//# TODO-1 report metrics
					//#    - use 'MyMetricsRegistry.metrics.counter()  API to get a counter
					//#    - for the name of the counter use  : traffic + domain2
					//#    - call 'inc' method on counter  (counter.inc())
					
					// Counter counter = MyMetricsRegistry.metrics.???("????");
					// counter.???
					
					
					//# TODO-2 : There is a subtle bug in the above counting. :-) 
					//# can you spot it?
					//# Hint : look at the debug print to see how you are getting the data
					
					//# TODO-3 : Bonus lab
					//# Look at Metrics API here : http://metrics.dropwizard.io/3.1.0/getting-started/
					//# can we use another Metric?
					

				}
				MyUtils.randomDelay(100, 300);
			}
		}
		consumer.close();
	}

	
}
