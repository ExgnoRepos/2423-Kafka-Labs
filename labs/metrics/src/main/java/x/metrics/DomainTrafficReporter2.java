package x.metrics;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;

import x.utils.MyConfig;
import x.utils.MyMetricsRegistry;

public class DomainTrafficReporter2 {
	private static final Logger logger = LoggerFactory.getLogger(DomainTrafficReporter2.class);

	public static void main(String[] args) throws Exception {
		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "domain-traffic-reporter2");
		config.put(ConsumerConfig.GROUP_ID_CONFIG, "domain-traffic-reporter2");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config.put("value.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");

		KafkaConsumer<String, Long> consumer = new KafkaConsumer<>(config);
		consumer.subscribe(Arrays.asList("domain_count")); // subscribe

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

					Counter counter = MyMetricsRegistry.metrics.counter("traffic." + domain2);
					long diff = traffic - counter.getCount();
					if (diff > 0) {
						counter.inc(diff);
						logger.debug("counter." + domain2 + ".inc(" + diff + ")");
					} else {
						counter.dec(diff);
						logger.debug("counter." + domain2 + ".dec(" + diff + ")");
					}

				}
				Utils.sleep(500);
			}
		}
		consumer.close();
	}

	
}
