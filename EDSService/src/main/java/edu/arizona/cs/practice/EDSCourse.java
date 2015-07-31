package edu.arizona.cs.practice;

public class EDSCourse {
	private String membership;
	private int courseId;
	private int encodedSection;

	public EDSCourse(String membership, int id, int encodedSection) {
		this.membership = membership;
		this.courseId = id;
		this.encodedSection = encodedSection;
	}

	public String getMembership() {
		return membership;
	}

	public int getCourseId() {
		return courseId;
	}

	public int getEncodedSection() {
		return encodedSection;
	}

	@Override
	public String toString() {
		return "EDSCourse [courseId=" + courseId + ", encodedSection=" + encodedSection + ", membership=" + membership + "]";
	}
	
}
