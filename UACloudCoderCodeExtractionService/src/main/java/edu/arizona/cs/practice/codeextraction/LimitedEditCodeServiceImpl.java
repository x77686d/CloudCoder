// CloudCoder - a web-based pedagogical programming environment
// Copyright (C) 2011-2012, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011-2012, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package edu.arizona.cs.practice.codeextraction;

import java.util.List;

import org.cloudcoder.app.client.rpc.EditCodeService;
import org.cloudcoder.app.server.persist.Database;
import org.cloudcoder.app.shared.model.ApplyChangeToTextDocument;
import org.cloudcoder.app.shared.model.Change;
import org.cloudcoder.app.shared.model.ChangeType;
import org.cloudcoder.app.shared.model.CloudCoderAuthenticationException;
import org.cloudcoder.app.shared.model.Pair;
import org.cloudcoder.app.shared.model.Problem;
import org.cloudcoder.app.shared.model.ProblemText;
import org.cloudcoder.app.shared.model.Quiz;
import org.cloudcoder.app.shared.model.QuizEndedException;
import org.cloudcoder.app.shared.model.StartedQuiz;
import org.cloudcoder.app.shared.model.SubmissionReceipt;
import org.cloudcoder.app.shared.model.TextDocument;
import org.cloudcoder.app.shared.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link EditCodeService}.
 * 
 * @author David Hovemeyer
 * 
 * Cloned by William Mitchell for batch-mode code extraction with cognizance of problem's deadline.
 */
public class LimitedEditCodeServiceImpl {
	private static final long serialVersionUID = 1L;
	private static final Logger logger=LoggerFactory.getLogger(LimitedEditCodeServiceImpl.class);


	public ProblemText doLoadCurrentText(User user, Problem problem) { // Made public for UACloudCoderCodeExtractionService
    	Change mostRecent = Database.getInstance().getMostRecentChangeByDeadline(user, problem.getProblemId());

    	if (mostRecent == null) {
    		// Presumably, user has never worked on this problem.
    		logger.debug("No changes recorded for user " + user.getId() + ", problem " + problem.getProblemId());
    		
    		// If the problem has a skeleton, it is the initial problem text.
    		// Otherwise, just use the empty string.
    		String initialText = problem.hasSkeleton() ? problem.getSkeleton() : "";
    		ProblemText initialProblemText = new ProblemText(initialText, true);
    		
    		return initialProblemText;
    	} else {
    		Change change = mostRecent; // result.get(0);
    		//System.out.format("Most recent change for %s and by %s was id=%d, type = %d\n", problem.getTestname(), user.getUsername(), change.getEventId(), change.getv

    		// If the Change is a full text change, great.
    		if (change.getType() == ChangeType.FULL_TEXT) {
    			return new ProblemText(change.getText(), false);
    		}

    		// Otherwise, find the last full-text change (if any) and
    		// apply all later changes.
    		
    		// Find the most recent full-text change.
    		Change fullText = Database.getInstance().getMostRecentFullTextChangeByDeadline(user, problem.getProblemId());
    		
    		// Text doc to accumulate changes.
    		TextDocument textDocument = new TextDocument();
    		
    		// Find the base revision (event id) that the deltas are relative to, if any.
    		int baseRev;
    		if (fullText != null) {
    			// found a full-text change to use as a base revision
    			textDocument.setText(fullText.getText());
    			baseRev = fullText.getEventId();
    		} else {
    			// no full-text change exists: base revision is implicitly the empty document
    			baseRev = -1;
    		}
    		
    		// Get all deltas that follow the base revision.
    		List<Change> deltaList = Database.getInstance().getAllChangesByDeadlineNewerThan(user, problem.getProblemId(), baseRev);
    		
    		// Apply the deltas to the base revision.
    		try {
        		ApplyChangeToTextDocument applicator = new ApplyChangeToTextDocument();
        		int deltaNum = 0;
	    		for (Change delta : deltaList) {
	    			//System.out.format("applying %d, event id = %d, %s\n", deltaNum++, delta.getEventId(), delta);
	    			applicator.apply(delta, textDocument);
	    		}
	    		return new ProblemText(textDocument.getText(), false);
    		} catch (RuntimeException e) {
    			// FIXME: should do something smarter than this 
    			logger.warn("Exception applying deltas to program text", e);
    			return new ProblemText(fullText != null ? fullText.getText() : "", false);
    		}
    	}
	}
}
