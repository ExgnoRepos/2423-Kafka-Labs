package x.lab08_avro;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

public class AvroProducer {
	private static final Logger logger = LoggerFactory.getLogger(AvroProducer.class);

	public static void main(final String[] args) {

		final Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		
		// Note: the KafkaAvroSerializer being used
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		
		// Note: we need an active schema registry to use this feature!
		props.put("schema.registry.url", "http://localhost:8081");

		try (KafkaProducer<Integer, Customer> producer = new KafkaProducer<>(props)) {
			for (int i = 1; i <= 10; i++) {

				final Customer customer = Customer.newBuilder().setId(i).setName("Homer Simpson " + i)
						.setEmail("homer-" + i + "@simpson.com").build();

				final ProducerRecord<Integer, Customer> customerRecord = new ProducerRecord<>("customer",
						customer.getId(), customer);
				RecordMetadata meta = producer.send(customerRecord).get();
				logger.debug(
						String.format(
								"Sent record [# %d] (key:%s, value:%s), "
										+ "meta (partition=%d, offset=%d, timestamp=%d), ",
								i, customerRecord.key(), customerRecord.value(), meta.partition(), meta.offset(),
								meta.timestamp()));
				Thread.sleep(300L);
			}
			producer.flush();

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
