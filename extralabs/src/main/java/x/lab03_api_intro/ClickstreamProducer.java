package x.lab03_api_intro;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.utils.ClickStreamGenerator;
import x.utils.ClickstreamData;
import x.utils.MyUtils;

public class ClickstreamProducer {
	private static final Logger logger = LoggerFactory.getLogger(ClickstreamProducer.class);

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		// TODO-1 : set the broker to 'localhost:9092'
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "???");
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "ClickstreamProducer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);

		for (int i = 1; i <= 10; i++) {
			ClickstreamData clickstream = ClickStreamGenerator.getClickStreamRecord();
			String clickstreamJSON = ClickStreamGenerator.getClickstreamAsJSON(clickstream);

			String key = clickstream.domain;
			String value = clickstreamJSON;

			/*-
			TODO-2 : let's construct a record
			 
			ProducerRecord takes three parameters 
			 - first param : topic = "clickstream" 
			 - second param : key = the key just constructed  (key)
			 - third param : value = the value just constructed (value)
			*/
			ProducerRecord<String, String> record = new ProducerRecord<>("???", "???", "???");

			// Experiment: measure the time taken for both send options

			long t1 = System.nanoTime();
			// sending without waiting for response
			producer.send(record);

			// sending and waiting for response
			// RecordMetadata meta = producer.send(record).get();
			long t2 = System.nanoTime();

			logger.debug(String.format("Sent record [%d] (key:%s, value:%s), " + "time took = %.2f ms", i, key, value,
					(t2 - t1) / 1e6));

			MyUtils.randomDelay(100, 500);

		}

		producer.close();

	}

}
