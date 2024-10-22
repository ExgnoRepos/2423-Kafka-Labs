package x.lab09_metrics;

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

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

import x.utils.MyMetricsRegistry;
import x.utils.MyUtils;

public class ConsumerWithMetrics {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerWithMetrics.class);

	private static final Meter meterConsumerEvents = MyMetricsRegistry.metrics.meter("consumer.events");
	private static final Timer timerConsumer = MyMetricsRegistry.metrics.timer("consumer.processing_time");
	private static final Histogram timerInKafka = MyMetricsRegistry.metrics.histogram("wait-time-in-kafka");

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "metrics-consumer");
		props.put(ConsumerConfig.CLIENT_ID_CONFIG, "Metrics Consumer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("clickstream")); // subscribe to topics

		logger.info("listening on clickstream topic");
		int msgCount = 0;

		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
				if (records.count() == 0)
					continue;
				for (ConsumerRecord<String, String> record : records) {
					msgCount++;

					// calculate time spent in Kafka
					long timeArrivedInKafka = record.timestamp();
					long currentTime = System.currentTimeMillis();
					long timeSpentInKafka = currentTime - timeArrivedInKafka;
					timerInKafka.update(timeSpentInKafka);
					logger.debug(("Kafka arrival time : " + timeArrivedInKafka + ",  currentTime :" + currentTime
							+ ",  wait time : " + (currentTime - timeArrivedInKafka)));
					

					logger.debug(String.format("Received message [%d] : [%s]", msgCount, record));

					// Mark the meter
					meterConsumerEvents.mark();

					// Measure processing time
					Timer.Context context = timerConsumer.time();
					MyUtils.randomDelay(100, 500);
					context.stop();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			consumer.close();
		}

	}
}