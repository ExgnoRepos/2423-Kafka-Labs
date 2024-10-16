package x.benchmark;

import java.text.NumberFormat;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.MyConfig;

enum SendMode {
	SYNC, ASYNC, FIRE_AND_FORGET
}

public class BenchmarkProducer implements Runnable, Callback {
	private static final Logger logger = LoggerFactory.getLogger(BenchmarkProducer.class);

	private final String topic;
	private final int maxMessages;
	private final SendMode sendMode;
	private final Properties props;
	NumberFormat formatter = NumberFormat.getInstance();

	private final KafkaProducer<String, String> producer;

	// topic, how many messages to send, and send mode
	public BenchmarkProducer(String topic, int maxMessages, SendMode sendMode) {
		this.topic = topic;
		this.maxMessages = maxMessages;
		this.sendMode = sendMode;

		this.props = new Properties();
		this.props.put("bootstrap.servers", MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		this.props.put("client.id", "BenchmarkProducer");
		this.props.put("acks", "all");
		this.props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		this.props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		this.producer = new KafkaProducer<>(props);
	}

	@Override
	public void run() {

		int numMessages = 0;
		long t1, t2;
		long start = System.nanoTime();
		String clickstream = "1451635200020,2.3.3.4,user_93,clicked,facebook.com,campaign_1,74";
		while ((numMessages < this.maxMessages)) {
			numMessages++;
			ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, "" + numMessages, clickstream);
			t1 = System.nanoTime();
			try {

				switch (this.sendMode) {
				case FIRE_AND_FORGET:
					producer.send(record);
					break;
				case SYNC:
					producer.send(record).get();
					break;
				case ASYNC:
					producer.send(record, this);
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

	public static void main(String[] args) throws Exception {
		logger.info("Bootstrap servers: " + MyConfig.BOOTSTRAP_SERVERS_DOCKERIZED);
		
		//BenchmarkProducer producer = new BenchmarkProducer("benchmark1", 1000000, SendMode.FIRE_AND_FORGET);
		//BenchmarkProducer producer = new BenchmarkProducer("benchmark1", 100000, SendMode.ASYNC);
		BenchmarkProducer producer = new BenchmarkProducer("benchmark1", 100000, SendMode.SYNC);
		logger.info("== Producer starting.... : " + producer);
		Thread t1 = new Thread(producer);
		t1.start();
		t1.join(); // wait for thread to complete
		logger.info("== Producer done.\n\n");


	}

}
