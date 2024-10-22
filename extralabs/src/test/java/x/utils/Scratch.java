package x.utils;

import java.util.Properties;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Scratch {

  public void test1() {

    Properties props = new Properties();
    props.put("enable.auto.commit", "false"); // must disable
    KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props);

    while (true) {
      ConsumerRecords<Integer, String> records = consumer.poll(100);
      for (ConsumerRecord<Integer, String> record : records) {
        System.out
            .println(String.format("topic = %s, partition = %s, offset = %d",
                record.topic(), record.partition(), record.offset()));
      }
      try {
        consumer.commitSync();
      } catch (CommitFailedException e) {
        e.printStackTrace();
      }
      finally {
    	  	consumer.close();
      }
    }

  }

}
