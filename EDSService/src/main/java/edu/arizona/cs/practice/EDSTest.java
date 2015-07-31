package edu.arizona.cs.practice;

import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EDSTest {  
	private static final Logger logger = LoggerFactory.getLogger(EDSService.class);

	public static void main(String[] args) throws Exception {

		logger.info("This is from EDSTest");

		EDSService edsService = new EDSService();
		if (args.length == 0)
			args = new String[] {"jmajor564"};

		Properties p = new Properties();
		p.load(new FileInputStream("/users/whm/mse/cloudcoder/uacs.properties"));
		for (Object key: p.keySet()) {
			String value = p.getProperty((String)key);
			System.out.format("%s -> %s\n", key, value);
		}
		System.out.println(p);

		EDSConfig edsConfig = new EDSConfig(p);

		for (String netid: args) {
			EDSData edsData = edsService.getData(netid, edsConfig);
			System.out.format("netid = '%s', last = '%s', given = '%s', courses = %s\n",
					edsData.getNetid(), edsData.getLastName(), edsData.getGivenName(), edsData.getCourses());
		}

	}



}
