package x.utils;

/*
import java.nio.file.Files;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.MockTime;
import kafka.utils.TestUtils;
import kafka.utils.Time;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import kafka.zk.EmbeddedZookeeper;

public class RunEmbeddedKafka {

  private static final String ZKHOST = "127.0.0.1";
  private static final String KAFKA_HOST = "127.0.0.1";
  private static final String KAFKA_PORT = "9092";
  private static final String TOPIC = "test";

  @Test
  public void startKafka() throws Exception {
    EmbeddedZookeeper zkServer = new EmbeddedZookeeper();
    String zkConnect = ZKHOST + ":" + zkServer.port();
    ZkClient zkClient =
        new ZkClient(zkConnect, 30000, 30000, ZKStringSerializer$.MODULE$);
    ZkUtils zkUtils = ZkUtils.apply(zkClient, false);

    Properties kafkaProperties = new Properties();
    kafkaProperties.setProperty("zookeeper.connect", zkConnect);
    kafkaProperties.setProperty("broker.id", "0");
    // kafkaProperties.setProperty("log.dirs", "/tmp/kafka-logs");
    kafkaProperties.setProperty("log.dirs",
        Files.createTempDirectory("kafka-").toAbsolutePath().toString());

    kafkaProperties.setProperty("listeners",
        "PLAINTEXT://" + KAFKA_HOST + ":" + KAFKA_PORT);

    KafkaConfig config = new KafkaConfig(kafkaProperties);
    Time mock = new MockTime();
    KafkaServer kafkaServer = TestUtils.createServer(config, mock);

    System.out.print("Enter to terminate Kafka:");
    String input = System.console().readLine();

    kafkaServer.shutdown();
    zkClient.close();
    zkServer.shutdown();

  }

}

*/