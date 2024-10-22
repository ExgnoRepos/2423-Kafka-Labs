package x.lab09_metrics;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
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

	private static final Meter meterProducerEvents = MyMetricsRegistry.metrics.meter("producer.events");

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, MyConfig.DEFAULT_BOOTSTRAP_SERVERS);
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "ClickstreamProducer"); // client.id
	    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		KafkaProducer<String, String> producer = new KafkaProducer<>(props);

		boolean keepRunning = true;
		long eventsSent = 0;
		while (keepRunning) {
			ClickstreamData clickstream = ClickStreamGenerator.getClickStreamRecord();
			String clickstreamJSON = ClickStreamGenerator.getClickstreamAsJSON(clickstream);

			String key = clickstream.domain;
			String value = clickstreamJSON;

			ProducerRecord<String, String> record = new ProducerRecord<>(MyConfig.TOPIC_CLICKSTREAM, key,
					clickstreamJSON);
			eventsSent++;
			long t1 = System.nanoTime();
			RecordMetadata meta = producer.send(record).get();
			long t2 = System.nanoTime();

			logger.debug(String.format(
					"Sent record [%d] (key:%s, value:%s), " + "meta (partition=%d, offset=%d, timestamp=%d), "
							+ "time took = %.2f ms",
					eventsSent, key, value, meta.partition(), meta.offset(), meta.timestamp(), (t2 - t1) / 1e6));

			meterProducerEvents.mark();

			MyUtils.randomDelay(300, 1000);

		}

		producer.close();

	}

}
