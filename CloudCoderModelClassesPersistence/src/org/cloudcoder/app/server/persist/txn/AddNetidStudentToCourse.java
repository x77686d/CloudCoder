package org.cloudcoder.app.server.persist.txn;

//CloudCoder - a web-based pedagogical programming environment
//Copyright (C) 2011-2013, Jaime Spacco <jspacco@knox.edu>
//Copyright (C) 2011-2013, David H. Hovemeyer <david.hovemeyer@gmail.com>
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

import java.sql.Connection;
import java.sql.SQLException;

import org.cloudcoder.app.server.persist.util.AbstractDatabaseRunnableNoAuthException;
import org.cloudcoder.app.server.persist.util.ConfigurationUtil;
import org.cloudcoder.app.shared.model.CourseRegistrationType;

/**
* Transaction to add UA student to a course
*/
public class AddNetidStudentToCourse  extends
		AbstractDatabaseRunnableNoAuthException<Boolean> {

	private final int studentId;
	private final int courseId;
	private final int encodedSection;

	public AddNetidStudentToCourse(int studentId, int courseId,
			int encodedSection) {
		this.studentId = studentId;
		this.courseId = courseId;
		this.encodedSection = encodedSection;
	}


	@Override
	public Boolean run(Connection conn) throws SQLException {
		boolean result = ConfigurationUtil.registerUser(conn, studentId, courseId, CourseRegistrationType.STUDENT, encodedSection);
		
		return result;
	}

	@Override
	public String getDescription() {
		return " adding Netid student to course if needed";
	}
}