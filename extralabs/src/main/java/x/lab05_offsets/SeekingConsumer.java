package x.lab05_offsets;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeekingConsumer {
	private static final Logger logger = LoggerFactory.getLogger(SeekingConsumer.class);
	private static final String TOPIC = "test";

  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("group.id", "seeking1");
    props.put("key.deserializer",
        "org.apache.kafka.common.serialization.StringDeserializer");
    props.put("value.deserializer",
        "org.apache.kafka.common.serialization.StringDeserializer");
    KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props);
    consumer.subscribe(Collections.singletonList(TOPIC)); // subscribe
    
    logger.info("listening on  topic : " + TOPIC);
    
    // try to get partitions before polling.  do we get any partitions assigned?
    logger.info("Before polling: assigned partitions # " + consumer.assignment().size());
    for (TopicPartition p : consumer.assignment()) {
    	logger.debug("Before polling, assigned partition : " + p);
    }

    int read = 0;
    while (read < 5) {
      ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofMillis(100));
      
      logger.info("After polling: assigned partitions #" + consumer.assignment().size());
      if (consumer.assignment().size() == 0) {
    	  logger.info("After polling: no partitions assigned yet.  keep polling");
    	  continue;
      }
      
      // if we are here, we have partitions assigned
      for (TopicPartition p : consumer.assignment()) {
      	logger.debug("After polling, assigned partition : " + p);
      }
      
      // grab the first partition...
      TopicPartition partition = consumer.assignment().iterator().next();
      logger.info ("Accessing partition : " + partition);
      
      /*- TODO- go to specific offsets
       *    - read the first message
       *    - read the last message
       *    - read message at offset 5
       *
       *  Reference : look at various seek options available here
       *  https://kafka.apache.org/0100/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
       */
      logger.debug("seeking to beginning of partition " + partition);
      consumer.seekToBeginning(Collections.singletonList(partition));

      // logger.debug ("seeking to end of partition " + partition);
      //consumer.seekToEnd(Collections.singletonList(partition));


      // logger.debug ("seeking to position #5 of " + partition);
      // consumer.seek(new TopicPartition("test", 0), 5);
      
      logger.debug ("current position " + consumer.position(partition));
      for (ConsumerRecord<Integer, String> record : records) {
        read++;
        logger.debug("Received message : " + record);
        break; // only process first message
      }

    }
    consumer.close();
  }
}
