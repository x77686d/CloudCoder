package edu.arizona.cs.practice;

import java.util.ArrayList;

public class EDSData {
	private String netid;
	private String lastName;
	private String givenName;
	private ArrayList<EDSCourse> courses;
	
	public EDSData(String netid, String lastName, String givenName, ArrayList<EDSCourse> courses) {
		this.netid = netid;
		this.lastName = lastName;
		this.givenName = givenName;
		this.courses = courses;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the givenName
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * @return the courses
	 */
	public ArrayList<EDSCourse> getCourses() {
		return courses;
	}

	public String getNetid() {
		return netid;
	}
	
	public String toString() {
		return String.format("EDSData [netid=%s, lastName=%s, givenName=%s, courses=%s]",
				netid, lastName, givenName, courses);
	}
	
}
