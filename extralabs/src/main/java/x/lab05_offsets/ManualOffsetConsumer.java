package x.lab05_offsets;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManualOffsetConsumer {

	private static final Logger logger = LoggerFactory.getLogger(ManualOffsetConsumer.class);

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", "group_manual_offset");
		props.put("auto.offset.reset", "earliest");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		// TODO-1: Set 'enable.auto.commit' to 'false'
		props.put("enable.auto.commit", "???");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList("test"));

        logger.info("Listening on topic: test...");

		int numMessages = 0;
		while (true) {

			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

			int count = records.count();
			if (count == 0)
				continue;

			logger.debug("Got " + count + " messages");

			for (ConsumerRecord<String, String> record : records) {
				numMessages++;
				logger.debug("Received message [" + numMessages + "] : " + record);
			}

			// print offsets
			Set<TopicPartition> partitions = consumer.assignment();
			for (TopicPartition p : partitions) {
				long pos = consumer.position(p);
				logger.debug("OFFSET : partition:" + p.partition() + ", offset:" + pos);
			}

			/*-
			 TODO-2: - do a few runs without calling 'commitSync' 

                         Observe the output

                         Then turn on commitsync and do a few runs.

                         Observe the output
                         
			 */
			// consumer.commitSync();

		}

	}
}
