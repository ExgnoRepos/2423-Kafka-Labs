package x.lab08_avro;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;

public class AvroConsumer {
	private static final Logger logger = LoggerFactory.getLogger(AvroConsumer.class);

	@SuppressWarnings("InfiniteLoopStatement")
	public static void main(final String[] args) {
		final Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "avro-consumer-1");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);

		// Note: AvroDeserializer
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

		// Note: We need the schema registry for the example to work!
		props.put("schema.registry.url", "http://localhost:8081");

		try (final KafkaConsumer<Integer, Customer> consumer = new KafkaConsumer<>(props)) {
			consumer.subscribe(Collections.singletonList("customer"));
			
			logger.info("Consumer listening on topic: customer");

			int count = 0;
			while (true) {
				final ConsumerRecords<Integer, Customer> records = consumer.poll(Duration.ofMillis(1000));
				for (final ConsumerRecord<Integer, Customer> record : records) {
					count++;
					final Integer key = record.key();
					final Customer value = record.value();

					logger.debug(String.format(
							"Received record [# %d] (key:%s, value:%s), " + "meta (partition=%d, offset=%d), ", count,
							record.key(), record.value(), record.partition(), record.offset()));
				}
			}

		}
	}
}
