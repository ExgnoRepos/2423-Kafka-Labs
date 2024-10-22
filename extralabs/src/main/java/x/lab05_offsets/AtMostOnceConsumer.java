package x.lab05_offsets;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AtMostOnceConsumer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AtMostOnceConsumer.class);

	private final String topic;
	private final KafkaConsumer<String, String> consumer;
	private boolean keepRunning = true;
	NumberFormat formatter = NumberFormat.getInstance();

	public AtMostOnceConsumer(String topic) {
		this.topic = topic;
		Properties props = new Properties();
		/*
		 * To implement At Most Once processing, we need to set auto-commit to a small
		 * value
		 */
		// TODO-1 : set servers to "localhost:9092"

		props.put("bootstrap.servers", "???");
		props.put("group.id", "group1");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		// TODO-2 : Set 'enable.auto.commit' to true
		props.put("???", "???");
		// TODO-2a : Set 'auto.commit.interval.ms' to a lower timeframe.
		props.put("???", "???");

		this.consumer = new KafkaConsumer<>(props);
		this.consumer.subscribe(Arrays.asList(this.topic));
	}

	@Override
	public void run() {
		int numMessages = 0;
		while (keepRunning) {
			// TODO-3 : increase time milis time from 0 to desirable number
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(0));

			// TODO-2 :
			// calculate how many records we have got
			// replace this with records.??? (hint : count)
			int count = 0;
			if (count == 0)
				continue;
			logger.debug("Got " + count + " messages");

			for (ConsumerRecord<String, String> record : records) {
				numMessages++;
				logger.debug("Received message [" + numMessages + "] : " + record);

			}
		}

		// logger.info(this + " received " + numMessages);
		logger.info("Received " + numMessages);

		// TODO-4 : close consumer
		// consumer.???
	}

	public void stop() {
		this.keepRunning = false;
		consumer.wakeup();
	}

	@Override
	public String toString() {
		return "ClickstreamConsumer (topic=" + this.topic + ")";
	}

	public static void main(String[] args) throws Exception {
		/*
		 * TODO-5 : create a consumer AtmostonceConsumer takes only one parameter name
		 * of topic to listen to. Set it to "clickstream"
		 */
		AtMostOnceConsumer consumer = new AtMostOnceConsumer("???");

		Thread t1 = new Thread(consumer);
		logger.info("starting consumer... : " + consumer);
		t1.start();
		t1.join();
		logger.info("consumer shutdown.");

	}

}
