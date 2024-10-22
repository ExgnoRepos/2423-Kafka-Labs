package x.metrics;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Meter;

import x.utils.ClickStreamGenerator;
import x.utils.ClickstreamData;
import x.utils.MyConfig;
import x.utils.MyMetricsRegistry;
import x.utils.MyUtils;

public class ProducerWithMetrics {
	private static final Logger logger = LoggerFactory.getLogger(ProducerWithMetrics.class);

	private static final Meter meterProducerEvents = MyMetricsRegistry.metrics.meter("kafka-metrics1.producer.events");

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "kafka-metrics-producer");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);


		long eventsSent = 0;
		for (int i = 0; i < 1000; i++) {
			ClickstreamData clickstream = ClickStreamGenerator.getClickStreamRecord();
			String clickstreamJSON = ClickStreamGenerator.getClickstreamAsJSON(clickstream);
			String key = clickstream.domain;
			ProducerRecord<String, String> record = new ProducerRecord<>("clickstream", key, clickstreamJSON);
			eventsSent++;
			logger.debug("sending event #" + eventsSent + " : " + record);
			producer.send(record);

			meterProducerEvents.mark(); // record metrics

			MyUtils.randomDelay(300, 1000);

		}

		producer.close();

	}

}
