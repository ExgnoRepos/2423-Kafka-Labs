package x.utils;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ClickstreamProducer {
//	private static final Logger logger = LoggerFactory.getLogger(ClickstreamProducer.class);

  public static void main(String[] args) throws Exception {
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");
    props.put("client.id", "ClickstreamProducer");
    props.put("key.serializer",
        "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer",
        "org.apache.kafka.common.serialization.StringSerializer");

    KafkaProducer<String, String> producer = new KafkaProducer<>(props);

    Gson gson = new Gson();

    for (int i = 0; i < 20; i++) {
      String clickstreamJSON = ClickStreamGenerator.getClickstreamAsJSON();
      ClickstreamData clickstream =
          gson.fromJson(clickstreamJSON, ClickstreamData.class);
      
      // TODO-1 : 
      //     key = clickstream.domain
      //     value = clickstreamJSON
      String key = clickstream.domain;
      String value = clickstreamJSON;  
      
      // TODO-2 : send the clickstreamJSON data as value with DOMAIN as key
      ProducerRecord<String, String> record =
          new ProducerRecord<>("clickstream", key,  value);
//      logger.debug("sending : " + record);
      System.out.println("sending : " + record);
      producer.send(record);

    }

    producer.close();

  }

}
