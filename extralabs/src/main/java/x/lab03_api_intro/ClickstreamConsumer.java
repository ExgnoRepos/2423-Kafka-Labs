package x.lab03_api_intro;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ClickstreamConsumer {

	private static final Logger logger = LoggerFactory.getLogger(ClickstreamConsumer.class);


	public static void main(String[] args) throws Exception {
		
		Properties props = new Properties();
		// TODO-1 : set servers to "localhost:9092"
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "???");

		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "clickstream-consumer");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "clickstream-consumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		
		// TODO-2: subscribe to topic "clickstream"
		consumer.subscribe(Arrays.asList("???"));

		logger.info("Listening on clickstream topic");
		
		int numMessages = 0;
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

			int count = records.count();
			if (count == 0)
				continue;
			logger.debug("Got " + count + " messages");

			for (ConsumerRecord<String, String> record : records) {
				numMessages++;
				logger.debug("Received message [" + numMessages + "] : " + record);

			}
		}

		

	}

}
