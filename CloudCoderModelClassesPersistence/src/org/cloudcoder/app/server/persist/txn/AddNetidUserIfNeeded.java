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
import org.cloudcoder.app.shared.model.User;

/**
* Transaction to add a tag to a repository exercise.
*/
public class AddNetidUserIfNeeded extends
		AbstractDatabaseRunnableNoAuthException<User> {
	private final String netid;

	public AddNetidUserIfNeeded(String netid) {
		this.netid = netid;
	}

	@Override
	public User run(Connection conn) throws SQLException {
		User user = ConfigurationUtil.addNetidUserIfNeeded(conn, netid);
		
		return user;
	}


	@Override
	public String getDescription() {
		return " adding Netid user if needed";
	}
}