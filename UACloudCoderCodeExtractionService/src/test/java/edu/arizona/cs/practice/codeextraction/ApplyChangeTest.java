package edu.arizona.cs.practice.codeextraction;

import static org.junit.Assert.assertEquals;

import org.cloudcoder.app.shared.model.Change;
import org.cloudcoder.app.shared.model.ChangeType;
import org.cloudcoder.app.shared.model.TextDocument;
import org.junit.Test;

public class ApplyChangeTest {

	@Test
	public void testFullTextChange() {
		
		assertEquals(1,1);
		Change c = new Change(ChangeType.FULL_TEXT, 0, 0, 0, 0, 1484741590657L, 1, 608, "def f():\n    pass");

		TextDocument doc = new TextDocument();
		ApplyChangeToTextDocument applier = new ApplyChangeToTextDocument();
		applier.apply(c, doc);
		String text = doc.getText();
		System.out.println(text);
		assertEquals("def f():\n    pass\n", text);
	}
	
	@Test
	public void testDelete() {
		
		assertEquals(1,1);
		Change c = new Change(ChangeType.FULL_TEXT, 0, 0, 0, 0, 1484741590657L, 1, 608, "def f():\n    pass");

		TextDocument doc = new TextDocument();
		ApplyChangeToTextDocument applier = new ApplyChangeToTextDocument();
		applier.apply(c, doc);
		String text = doc.getText();
		System.out.println(text);
		assertEquals("def f():\n    pass\n", text);  // Seems to append a newline
		//
		// Row and column are ZERO-based
		applier.apply(new Change(ChangeType.REMOVE_TEXT, 0, 0, 0, 0, 1484741590657L, 1, 608, "d"), doc);
		
		text = doc.getText();
		System.out.println(text);
		assertEquals("ef f():\n    pass\n", doc.getText());
		
		applier.apply(new Change(ChangeType.REMOVE_TEXT, 1, 5, 1, 5, 1484741590657L, 1, 608, "a"), doc);
		
		text = doc.getText();
		System.out.println(text);
		assertEquals("ef f():\n    pss\n", doc.getText());
		
	}
	
	private Change buildChange1() {
/*
| event_id  | type | start_row | end_row | start_col | end_col | text_short | text                                       | id        | user_id | problem_id | type | timestamp     |
+-----------+------+-----------+---------+-----------+---------+------------+--------------------------------------------+-----------+---------+------------+------+---------------+
| 144001686 |    4 |         0 |       0 |         0 |       0 | NULL       | def number2letter(n):
    # your code here | 144001686 |       1 |        608 |    0 | 1484741590657 |
 */
		
		Change c = new Change(ChangeType.FULL_TEXT, 0, 0, 0, 0, 1484741590657L, 1, 608, "def number2letter(n):\n    # your code here");
		
		return c;
	}
	
/*
	private Change(ChangeType type, int sr, int sc, int er, int ec, long ts, int userId, int problemId) {
		this.type = type.ordinal();
		this.startRow = sr;
		this.startColumn = sc;
		this.endRow = er;
		this.endColumn = ec;

		this.event = new Event(userId, problemId, EventType.CHANGE, ts);

 */


/*
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
*/

}
