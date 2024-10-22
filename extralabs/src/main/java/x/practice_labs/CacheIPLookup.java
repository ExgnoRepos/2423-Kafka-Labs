package x.practice_labs;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CacheIPLookup implements FraudIPLookup, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	private Set<String> fraudIPs = new HashSet<>();
	
	public  CacheIPLookup() {
		// TODO: add more IPs to the fraudIP
		fraudIPs.add("3.3");
		fraudIPs.add("4.4");
	}

	@Override
	public boolean isFraudIP(String ipAddress) {
		String a_b = "x.y";
		if (ipAddress != null ) {
			String []tokens = ipAddress.split("\\."); // regex, so . must be escaped
			if (tokens.length == 4)
				a_b = tokens[0] + "." + tokens[1];
		}

		return fraudIPs.contains(a_b);
	}

}
