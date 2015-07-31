// CloudCoder - a web-based pedagogical programming environment
// Copyright (C) 2011-2014, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011-2014, David H. Hovemeyer <david.hovemeyer@gmail.com>
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

package edu.arizona.cs;

/**
 * @author whm
 *
 * Represents information from UA EDS that the netid is registered for a course this term, either as a student or an instructor.
 */
public class Registration {
	private RegistrationType type;
	private String courseNumber;
	private String section;

	public enum RegistrationType {
		STUDENT,
		INSTRUCTOR
	
	}
	public Registration(RegistrationType type, String courseNumber,
			String section) {
		this.type = type;
		this.courseNumber = courseNumber;
		this.section = section;
	}

	public RegistrationType getType() {
		return type;
	}

	public String getCourseNumber() {
		return courseNumber;
	}

	public String getSection() {
		return section;
	}
	
}
