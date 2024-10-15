package x.api;

import java.util.Arrays;
import java.util.Properties;
import java.time.Duration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.MyConfig;

public class SimpleConsumer {
	private static final Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		// we are setting 2 brokers here:
		//    'localhost:9092' is valid for accessing from host machine say from Eclipse
		//    'kafka1:19902' is valid for accessing kafka cluster from within dockers network
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "Simple Consumer");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		/*
		 * // Try the following to read-from-beginning
		 * props.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
		 * props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
		 * props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		 */
		logger.info("Bootstrap servers: " + MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("test")); // subscribe to topics

		logger.info("listening on test topic");
		int msgCount = 0;
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
			if (records.count() == 0)
				continue;
			logger.debug("Got " + records.count() + " messages");
			for (ConsumerRecord<String, String> record : records) {
				msgCount++;
				logger.debug(String.format("Received message [%d] : [%s]", msgCount, record));
			}
		}
	}
}