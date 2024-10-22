package x.lab04_benchmark;

import java.text.NumberFormat;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.utils.ClickStreamGenerator;
import x.utils.MyConfig;

enum SendMode {
	SYNC, ASYNC, FIRE_AND_FORGET
}

public class BenchmarkProducerSendModes implements Runnable, Callback {
	private static final Logger logger = LoggerFactory.getLogger(BenchmarkProducerSendModes.class);

	private final String topic;
	private final int maxMessages;
	private final SendMode sendMode;
	private final Properties props;
	NumberFormat formatter = NumberFormat.getInstance();

	private final KafkaProducer<String, String> producer;

	// topic, how many messages to send, and send mode
	public BenchmarkProducerSendModes(String topic, int maxMessages, SendMode sendMode) {
		this.topic = topic;
		this.maxMessages = maxMessages;
		this.sendMode = sendMode;

		this.props = new Properties();
		this.props.put("bootstrap.servers", "localhost:9092");
		this.props.put("client.id", "BenchmarkProducer");
		this.props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		this.props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		this.producer = new KafkaProducer<>(props);
	}

	@Override
	public void run() {

		int numMessages = 0;
		long t1, t2;
		long start = System.nanoTime();
		String clickstream = ClickStreamGenerator.getClickstreamAsCsv();
		while ((numMessages < this.maxMessages)) {
			numMessages++;
			// String clickstream = ClickStreamGenerator.getClickstreamAsJSON();
			ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, "" + numMessages, clickstream);
			t1 = System.nanoTime();
			try {

				switch (this.sendMode) {
                    case FIRE_AND_FORGET:
                        // TODO : send here
                        // producer.send(record);
                        break;
                    case SYNC:
                        // TODO : send here
                        // producer.send(record).get();
                        break;
                    case ASYNC:
                        // TODO : send here
                        // producer.send(record, this);
                        break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			t2 = System.nanoTime();


		}
		long end = System.nanoTime();
		producer.close(); // close connection

		// print summary
		logger.info("== " + toString() + " done.  " + formatter.format(numMessages) + " messages sent in "
				+ formatter.format((end - start) / 10e6) + " milli secs.  Throughput : "
				+ formatter.format((long) (numMessages * 10e9 / (end - start))) + " msgs / sec");

	}

	@Override
	public String toString() {
		return "Benchmark Producer (topic=" + this.topic + ", maxMessages=" + formatter.format(this.maxMessages)
				+ ", sendMode=" + this.sendMode + ")";
	}

	// Kafka callback
	@Override
	public void onCompletion(RecordMetadata meta, Exception ex) {
		if (ex != null) {
			logger.error("Callback :  Error during async send");
			ex.printStackTrace();
		}
		if (meta != null) {
			// logger.debug("Callback : Success sending message " + meta);
		}

	}

	// test driver
	public static void main(String[] args) throws Exception {

		for (SendMode sendMode : SendMode.values()) {
			// TODO : once the code is working, increase the number of events to a million (1000000)
			BenchmarkProducerSendModes producer = new BenchmarkProducerSendModes(MyConfig.TOPIC_BENCHMARK, 100000, sendMode);
			logger.info("== Producer starting.... : " + producer);
			Thread t1 = new Thread(producer);
			t1.start();
			t1.join(); // wait for thread to complete
			logger.info("== Producer done.\n\n");
		}

	}

}
