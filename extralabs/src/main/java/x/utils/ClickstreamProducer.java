package x.utils;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ClickstreamProducer {
	private static final Logger logger = LoggerFactory.getLogger(ClickstreamProducer.class);

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("client.id", "ClickstreamProducer");
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);

		Gson gson = new Gson();

		for (int i = 1; i <= 10; i++) {
			ClickstreamData clickstream = ClickStreamGenerator.getClickStreamRecord();
			String clickstreamJSON = ClickStreamGenerator.getClickstreamAsJSON(clickstream);

			String key = clickstream.domain;
			String value = clickstreamJSON;

			ProducerRecord<String, String> record = new ProducerRecord<>(MyConfig.TOPIC_CLICKSTREAM, key, value);
			logger.debug("sending record # " + i + " : " + record);

			long t1 = System.nanoTime();
			RecordMetadata meta = producer.send(record).get();
			long t2 = System.nanoTime();

			logger.debug(String.format(
					"Sent record [%d] (key:%s, value:%s), " + "meta (partition=%d, offset=%d, timestamp=%d), "
							+ "time took = %.2f ms",
					i, key, value, meta.partition(), meta.offset(), meta.timestamp(), (t2 - t1) / 1e6));

			MyUtils.randomDelay(100, 500);

		}

		producer.close();

	}

}
