package edu.arizona.cs.practice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EDSUtil {

	public static int encodeSection(String sectionSpec) {
		Pattern p = Pattern.compile("^(\\d{3})([A-Z]?)$");
		Matcher m = p.matcher(sectionSpec);
		
		if (!m.find()) {
			throw new RuntimeException("regex failed on section '" + sectionSpec + "'");
		}
		
		int sectionNumber = Integer.parseInt(m.group(1));
		String sectionLetter = m.group(2);
		int letterPart = 0;
		if (sectionLetter.length() != 0) {
			letterPart = sectionLetter.charAt(0) - 'A' + 1;
		}
		
		return sectionNumber * 100 + letterPart;
	}

	public static String decodeSection(int encoded) {
		String section = String.format("%03d", encoded / 100);
		if (encoded % 100 != 0.0) {
			section += String.format("%c", 64 + encoded % 100);
		}
		return section;
	}

	public static String extractSection(String courseSpec) {
		Pattern p = Pattern.compile("(\\d{3}[A-Z]?):learner$");
		Matcher m = p.matcher(courseSpec);
		if (!m.find()) {
			throw new RuntimeException("Can't find section in membership '" + courseSpec + "'");
		}
		
		return m.group(1);
					
	}

}
