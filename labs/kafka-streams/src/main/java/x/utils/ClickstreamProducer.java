package x.utils;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.MyConfig;

public class ClickstreamProducer {
	private static final Logger logger = LoggerFactory.getLogger(ClickstreamProducer.class);

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put("bootstrap.servers", MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		props.put("client.id", "ClickstreamProducer");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);

		for (int i = 0; i < 20; i++) {
			ClickstreamData clickstream = ClickStreamGenerator.getClickStreamRecord();
			String clickstreamJSON = ClickStreamGenerator.getClickstreamAsJSON(clickstream);

			String key = clickstream.domain;
			String value = clickstreamJSON;

			ProducerRecord<String, String> record = new ProducerRecord<>("clickstream", key, value);
			logger.debug("sending : " + record);
			producer.send(record);
		}

		producer.close();

	}

}
