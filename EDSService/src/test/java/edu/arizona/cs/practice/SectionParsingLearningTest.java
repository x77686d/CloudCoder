package edu.arizona.cs.practice;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class SectionParsingLearningTest {

	Pattern p = Pattern.compile("(\\d+)([A-Z]?):learner$");
	
	@Test
	public void test001() {
		Matcher m = p.matcher("arizona.edu:courses:2154:1:CSC:352:001:learner");
		assertTrue(m.find());
		assertEquals(2, m.groupCount());
		assertEquals("001", m.group(1));
		assertEquals("", m.group(2));
	}

	@Test
	public void test002C() {
		Matcher m = p.matcher("arizona.edu:courses:2152:DYN:CSC:127A:001C:learner");
		assertTrue(m.find());
		assertEquals(2, m.groupCount());
		assertEquals("001", m.group(1));
		assertEquals("C", m.group(2));
	}

	@Test
	public void testFailOnExtra() {
		Matcher m = p.matcher("arizona.edu:courses:2152:DYN:CSC:127A:001C:learner ");
		assertFalse(m.find());
	}
}
