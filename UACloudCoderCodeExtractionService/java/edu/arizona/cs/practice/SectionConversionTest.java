package edu.arizona.cs.practice;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SectionConversionTest {

	@Test
	public void testEncoding() {
		assertEquals(100, EDSUtil.encodeSection("001"));
		assertEquals(300, EDSUtil.encodeSection("003"));
		//assertEquals(-1, EDSUtil.encodeSection("1"));
		assertEquals(101, EDSUtil.encodeSection("001A"));
		assertEquals(102, EDSUtil.encodeSection("001B"));
		assertEquals(211, EDSUtil.encodeSection("002K"));
		assertEquals(1026, EDSUtil.encodeSection("010Z"));
	}

	@Test
	public void testDecoding() {
		assertEquals(EDSUtil.decodeSection(100), "001");
		assertEquals(EDSUtil.decodeSection(101), "001A");
		assertEquals(EDSUtil.decodeSection(211), "002K");
	}
	
	@Test
	public void testExtractSection() throws Exception {
		assertEquals("001A", EDSUtil.extractSection("arizona.edu:courses:2152:DYN:CSC:127A:001A:learner"));
		assertEquals("001K", EDSUtil.extractSection("arizona.edu:courses:2152:DYN:CSC:127A:001K:learner"));
		assertEquals("001", EDSUtil.extractSection("arizona.edu:courses:2154:1:CSC:352:001:learner"));
		assertEquals("007", EDSUtil.extractSection("arizona.edu:courses:2154:1:CSC:346:007:learner"));
	}

}
