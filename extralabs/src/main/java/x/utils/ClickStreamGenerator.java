package x.utils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class ClickStreamGenerator {
	//private static final Logger logger = LoggerFactory.getLogger(ClickStreamGenerator.class);
//	static Random rand = new Random(100L);
	static Random rand = new Random();

	private static long START_TIME = 1451635200 * 1000L; // 2015-01-01 00:00:00
															// in
	// ms
	private static long INC = 5; // ms
	private static AtomicLong currentTime = new AtomicLong(START_TIME);

	private static Gson gson = new Gson();

	public static int MAX_USERS = 100;
	public static int MAX_CAMPAIGNS = 10;
	public static int MAX_SESSIONS = 300;
	public static int MAX_IPS = 100;
	public static String[] DOMAINS = new String[] { "facebook.com", "gmail.com", "twitter.com", "youtube.com" };
	public static String[] ACTIONS = new String[] { "blocked", "viewed", "clicked" };

	static String getRandomIP() {
		return String.format("%d.%d.%d.%d", rand.nextInt(3)+1, rand.nextInt(3)+1, rand.nextInt(10), rand.nextInt(10));
	}
	public static ClickstreamData getClickStreamRecord() {
		ClickstreamData clickstream = new ClickstreamData();
		clickstream.timestamp = currentTime.addAndGet(INC);
		clickstream.user = "user_" + (rand.nextInt(MAX_USERS) + 1);
		clickstream.campaign = "campaign_" + (rand.nextInt(MAX_CAMPAIGNS) + 1);
		clickstream.domain = DOMAINS[rand.nextInt(DOMAINS.length)];
		clickstream.cost = rand.nextInt(100);
		clickstream.ip = getRandomIP();
		clickstream.action = ACTIONS[rand.nextInt(ACTIONS.length)];
		return clickstream;
	}
	
	public static String getClickstreamAsCsv (ClickstreamData clickstream)
	{
		return clickstream.timestamp + "," + clickstream.ip + "," + clickstream.user + "," + clickstream.action + ","
				+ clickstream.domain + "," + clickstream.campaign + "," + clickstream.cost ;
	}

	public static String getClickstreamAsCsv() {

		ClickstreamData clickstream = getClickStreamRecord();
		return getClickstreamAsCsv(clickstream);
		
	}

	public static String getClickstreamAsJSON(ClickstreamData clickstream) {
		String s = gson.toJson(clickstream);
		return s;
	}
	public static String getClickstreamAsJSON() {
		ClickstreamData clickstream = getClickStreamRecord();
		return getClickstreamAsJSON(clickstream);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(getClickstreamAsCsv());
		}
		System.out.println("\n\n");

		for (int i = 0; i < 10; i++) {
			System.out.println(getClickstreamAsJSON());
		}
	}

}
