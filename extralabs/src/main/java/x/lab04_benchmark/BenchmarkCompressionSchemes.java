package x.lab04_benchmark;

import java.text.NumberFormat;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import x.utils.ClickStreamGenerator;

public class BenchmarkCompressionSchemes implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(BenchmarkCompressionSchemes.class);

	private final String topic;
	private final int maxMessages;
	private final Properties props;
	private final String compressionType;
	private boolean keepRunning;
	NumberFormat formatter = NumberFormat.getInstance();

	private final KafkaProducer<String, String> producer;

	// topic, how many messages to send
	public BenchmarkCompressionSchemes(String topic, int maxMessages, String compressionType) {
		this.topic = topic;
		this.maxMessages = maxMessages;
		this.compressionType = compressionType;
		this.keepRunning = true;

		this.props = new Properties();
		// TODO : start with your own kafka "localhost:9092"
		// once the program is working, pair up with another student
		// change the host to point to their kafka node! :-)
		// recommend using 'internal IP' address of their machine
        // Here is how you find the internal IP : 
        //      from the SSH session, issue the following command 
        //              ifconfig 
        //      look at the output for 'eth0' section.
        //  Here is sample output, and my internal ip address is 11.222.333.444 
        // (fake don't use this IP address)
        //         eth0      Link encap:Ethernet  HWaddr 02:f1:6e:7d:9e:de
        //                   inet addr:11.222.333.444  Bcast:172.31.47.255  Mask:255.255.240.0
        
		this.props.put("bootstrap.servers", "localhost:9092");
		this.props.put("client.id", "CompressedProducer");

//		this.props.put("key.serializer", "org.apache.kafka.common.serialization.BytesSerializer");
//		this.props.put("value.serializer", "org.apache.kafka.common.serialization.BytesSerializer");

		this.props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		this.props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		if (this.compressionType != null)
			this.props.put("compression.type", this.compressionType);

		this.producer = new KafkaProducer<>(props);
	}

	@Override
	public void run() {

		int numMessages = 0;
		long start = System.nanoTime();
		long totalMsgSize1 = 0L, totalKeySize1 = 0L, totalValueSize1 = 0L;
		int keySize1 = -1;
		int valueSize1 = -1;
		String clickstreamJSON = ClickStreamGenerator.getClickstreamAsJSON();
		String value = clickstreamJSON;
		while (this.keepRunning && (numMessages < this.maxMessages)) {
			numMessages++;

			String key = "" + numMessages;
			
			keySize1 = key.getBytes().length;
			valueSize1 = value.getBytes().length;
			if (keySize1 > 0)
				totalKeySize1 += keySize1;
			if (valueSize1 > 0)
				totalValueSize1 += valueSize1;
			totalMsgSize1 = totalKeySize1 + totalValueSize1;
			
			ProducerRecord<String, String> record = new ProducerRecord<>(this.topic, key, value);
			producer.send(record);
		}
		long end = System.nanoTime();

		producer.close();

		// print summary
		logger.info("\n== " + toString() + " done.  " + formatter.format(numMessages) + " messages sent in "
				+ formatter.format((end - start) / 10e6) + " milli secs.  Throughput : "
				+ formatter.format((long) (numMessages * 10e9 / (end - start))) + " msgs / sec\n");
		
		logger.info("\n    Sent " + formatter.format(numMessages) + " messages\n"
				+ "    Total serialized key size = " + formatter.format(totalKeySize1) + " bytes \n"
				+ "    Total serialized value size = " + formatter.format(totalValueSize1) + " bytes \n"
				+ "    Total message size = (keySize + valueSize) = " + formatter.format(totalMsgSize1) + " bytes \n");


	}

	public void stop() {
		this.keepRunning = false;
	}

	@Override
	public String toString() {
		return "Compression Benchmark  Producer (topic=" + this.topic + ", maxMessages="
				+ formatter.format(this.maxMessages) + ", compressionType=" + this.compressionType + ")";
	}

	// test driver
	public static void main(String[] args) throws Exception {

		// TODO : start with 1000 messages, and then increase it to a million (1000000)

		// TODO-2: Try different compression types
		// valid values are : none, gzip,snappy,lz4
		// See documentation for values to give to this property
		// https://kafka.apache.org/documentation/#configuration

		BenchmarkCompressionSchemes producer = new BenchmarkCompressionSchemes("compression", 100000, "snappy");

		logger.info("Producer starting.... : " + producer);
		Thread t1 = new Thread(producer);
		t1.start();
		t1.join(); // wait for thread to complete
		logger.info("Producer done.");

	}

}
