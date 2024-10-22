package x.lab04_benchmark;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.time.Duration;

public class CompressedConsumer implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(CompressedConsumer.class);

	private final String topic;
	private final KafkaConsumer<String, String> consumer;
	private boolean keepRunning = true;
	NumberFormat formatter = NumberFormat.getInstance();

	public CompressedConsumer(String topic) {
		this.topic = topic;
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "group1");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

//		props.put("key.serializer", "org.apache.kafka.common.serialization.ByteSerializer");
//		props.put("value.serializer", "org.apache.kafka.common.serialization.ByteSerializer");

		this.consumer = new KafkaConsumer<>(props);
		this.consumer.subscribe(Arrays.asList(this.topic));
	}

	@Override
	public void run() {
		int numMessages = 0;
		int timesGotZeroMsgs = 0;
		int maxTimesGotZeroMsgs = 10;
		long totalMsgSize1 = 0L, totalKeySize1 = 0L, totalValueSize1 = 0L;
		int keySize1 = -1;
		int valueSize1 = -1;

		while (keepRunning && (timesGotZeroMsgs < maxTimesGotZeroMsgs)) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
			int count = records.count();
			if (count == 0) {
				timesGotZeroMsgs++;
				logger.info("zero messages received, will exit automatically after "
						+ (maxTimesGotZeroMsgs - timesGotZeroMsgs) + "  more tries with zero messages");
				continue;
			}

			// logger.debug("Got " + count + " messages");
			for (ConsumerRecord<String, String> record : records) {
				numMessages++;
				// logger.debug("Received message [" + numMessages + "] : " + record);

				// size1
				keySize1 = record.key().getBytes().length;
				valueSize1 = record.value().getBytes().length;
				if (keySize1 > 0)
					totalKeySize1 += keySize1;
				if (valueSize1 > 0)
					totalValueSize1 += valueSize1;
				totalMsgSize1 = totalKeySize1 + totalValueSize1;

			}
		}

		logger.info("\n    Received " + formatter.format(numMessages) + " messages\n"
				+ "    Total serialized key size = " + formatter.format(totalKeySize1) + " bytes \n"
				+ "    Total serialized value size = " + formatter.format(totalValueSize1) + " bytes \n"
				+ "    Total message size = (keySize + valueSize) = " + formatter.format(totalMsgSize1) + " bytes \n");

		consumer.close();
	}

	public void stop() {
		this.keepRunning = false;
		consumer.wakeup();
	}

	@Override
	public String toString() {
		return "Compress Consumer (topic=" + this.topic + ")";
	}

	public static void main(String[] args) throws Exception {

		CompressedConsumer consumer = new CompressedConsumer("compression");

		Thread t1 = new Thread(consumer);
		logger.info("starting consumer... : " + consumer);
		t1.start();
		t1.join();
		logger.info("consumer shutdown.");

	}

}
