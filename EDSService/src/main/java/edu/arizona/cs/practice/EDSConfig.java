package edu.arizona.cs.practice;

import java.util.HashMap;
import java.util.Properties;

public class EDSConfig {
	private String login;
	private String password;
	private String courseMembershipPrefix;
	private String courseMappings;
	private HashMap<String, Integer> membershipToCourseIdMap;
	
	/**
	 * @return the membershipToCourseIdMap
	 */
	public HashMap<String, Integer> getMembershipToCourseIdMap() {
		return membershipToCourseIdMap;
	}

	public EDSConfig(Properties properties) {
		login = properties.getProperty("uacs.eds.login");
		password = properties.getProperty("uacs.eds.password");
		courseMembershipPrefix = properties.getProperty("uacs.eds.coursePrefix");
		courseMappings = properties.getProperty("uacs.eds.courseMappings");
		if (login == null || password == null || courseMembershipPrefix == null || courseMappings == null) {
			throw new RuntimeException("EDSConfig properties are incomplete");
		}
		buildMembershipMap();
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the coursePrefix
	 */
	public String getCourseMembershipPrefix() {
		return courseMembershipPrefix;
	}

   private void buildMembershipMap() {
		membershipToCourseIdMap = new HashMap<String,Integer>();
		for (String spec: courseMappings.split("/")) {
			String parts[] = spec.split(" is ");
			membershipToCourseIdMap.put(parts[0], Integer.parseInt(parts[1]));
		}
	}
}
