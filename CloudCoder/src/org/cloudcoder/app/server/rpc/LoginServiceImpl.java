// CloudCoder - a web-based pedagogical programming environment
// Copyright (C) 2011-2013, Jaime Spacco <jspacco@knox.edu>
// Copyright (C) 2011-2013, David H. Hovemeyer <david.hovemeyer@gmail.com>
// Copyright (C) 2013, York College of Pennsylvania
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

package org.cloudcoder.app.server.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.cloudcoder.app.client.rpc.LoginService;
import org.cloudcoder.app.server.login.ILoginProvider;
import org.cloudcoder.app.server.login.LoginProviderServletContextListener;
import org.cloudcoder.app.server.persist.Database;
import org.cloudcoder.app.server.persist.InitErrorList;
import org.cloudcoder.app.server.persist.util.ConfigurationUtil;
import org.cloudcoder.app.server.persist.util.DBUtil;
import org.cloudcoder.app.shared.model.ConfigurationSetting;
import org.cloudcoder.app.shared.model.ConfigurationSettingName;
import org.cloudcoder.app.shared.model.InitErrorException;
import org.cloudcoder.app.shared.model.LoginSpec;
import org.cloudcoder.app.shared.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.cs.Registration.RegistrationType;
import edu.arizona.cs.practice.EDSConfig;
import edu.arizona.cs.practice.EDSCourse;
import edu.arizona.cs.practice.EDSData;
import edu.arizona.cs.practice.EDSService;

/**
 * Implementation of {@link LoginService}.
 * The actual authentication decision is made by whatever
 * {@link ILoginProvider} implementation was created by
 * {@link LoginProviderServletContextListener}: this allows multiple
 * login providers to be supported (chosen at configuration time.)
 * 
 * @author David Hovemeyer
 * @author Jaime Spacco
 */
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {
	private static final long serialVersionUID = 1L;
	private static final Logger logger=LoggerFactory.getLogger(LoginServiceImpl.class);
    
    /**
     * Set this to true to have very short session timeouts.
     * Useful for testing that RPC calls that fail because of session
     * timeouts can be successfully completed following a successful
     * call to CloudCoderPage.recoverFromServerSessionTimeout().
     * Should NOT be set to true for production!
     */
    private static boolean DEBUG_SESSION_TIMEOUTS = false;

    /**
     * Default session timeout in seconds.  Defaults to 30 minutes.
     */
    private static final int SESSION_TIMEOUT_IN_SECONDS = 30 * 60;
    
    @Override
    public LoginSpec getLoginSpec() {
    	ILoginProvider provider = LoginProviderServletContextListener.getProviderInstance();
    	
    	LoginSpec loginInfo = new LoginSpec();
    	
    	ConfigurationSetting setting = Database.getInstance().getConfigurationSetting(ConfigurationSettingName.PUB_TEXT_INSTITUTION);
    	String institutionName = setting != null ? setting.getValue() : "Unknown institution";
    	loginInfo.setInstitutionName(institutionName);
    	
    	
    	boolean usernamePasswordRequired = provider.isUsernamePasswordRequired();
		loginInfo.setUsernamePasswordRequired(usernamePasswordRequired);
    	
		if (!usernamePasswordRequired) {
	    	loginInfo.setPreAuthorizedUsername(provider.getPreAuthorizedUsername(getThreadLocalRequest()));
		}
		
		return loginInfo;
    }

	@Override
	public User login(String userName, String password) {
	    // Can this method be called anywhere?

	    // Does AdminAuthorizationFilter have access to the ServletConfig?
        
		User user=null;

		// Get the configured ILoginProvider instance
		ILoginProvider provider = LoginProviderServletContextListener.getProviderInstance();
		
		// Try to log in
		user = provider.login(userName, password, getThreadLocalRequest());
		
	    if (user == null) {
	    	logger.info("Login failure for user {}", userName);
	    }

		if (user != null) {
			// Set User object in server HttpSession so that other
			// servlets will know that the client is logged in
			HttpSession session = getThreadLocalRequest().getSession();
			session.setAttribute(SessionAttributeKeys.USER_KEY, user);
			
			// Set session timeout.
			int maxInactive = SESSION_TIMEOUT_IN_SECONDS;
			if (DEBUG_SESSION_TIMEOUTS) {
				// This is useful for testing automatic retry of RPC calls
				// that fail because of a server session timeout:
				// expires server sessions after 20 seconds of inactivity.
				maxInactive = 20;
			}
			session.setMaxInactiveInterval(maxInactive);
		}
		
		return user;
	}

    @Override
	public void logout() {
		HttpSession session = getThreadLocalRequest().getSession();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> attributeNames = (Enumeration<String>) session.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attr = attributeNames.nextElement();
			session.removeAttribute(attr);
		}
	}
    
	/* (non-Javadoc)
	 * @see org.cloudcoder.app.client.rpc.LoginService#getUser()
	 */
	@Override
	public User getUser() throws InitErrorException {
		// Special case: this is the first RPC call that is made by the
		// client.  If a fatal init error occurred here in the server side
		// of the webapp, throw an InitErrorException to let the client
		// know to display a diagnostic page (that the cloudcoder admin
		// can use to resolve the issue.)
		if (InitErrorList.instance().hasErrors()) {
			throw new InitErrorException();
		}
		
		return (User) getThreadLocalRequest().getSession().getAttribute(SessionAttributeKeys.USER_KEY);
	}
	
	@Override
	public String[] getInitErrorList() {
		List<String> initErrorList = InitErrorList.instance().getErrorList();
		return initErrorList.toArray(new String[initErrorList.size()]);
	}
	
	@Override
	public User loginWithTicket(String ticket) {
		logger.info("ticket = {}", ticket);
		try {
			String netid = null;

			if (ticket.charAt(0) == '@' && "true".equals(DBUtil.getConfigProperties().getProperty("testMode"))) { // for testing!
				logger.info("accepting TESTMODE ticket");
				String[] ticketParts = ticket.substring(1).split("/");
				if (ticketParts[0].equals("yes")) {
					netid = ticketParts[1];
				}
				logger.info("TESTMODE ticket netid = {}", netid);
			} else {

				URL validate;
				validate = new URL("https://webauth.arizona.edu/webauth/validate?service=https://practice.cs.arizona.edu&ticket=" + ticket);
				BufferedReader in = new BufferedReader(new InputStreamReader(validate.openStream()));

				String status = in.readLine();
				logger.info("webauth line 1 (status) = '{}'", status);
				System.out.println("read status = " + status);
				if ("yes".equals(status)) {
					netid = in.readLine();
					logger.info("webauth line 2 (netid) = '{}'", netid);
				}
				in.close();
			}
			
			if (netid == null)
				return null;

			//
			// See if the user has an account
			User user = Database.getInstance().getUserWithoutAuthentication(netid);
			logger.info("getUserWithoutAuthentication({}) = {}", netid, user);
			
			//
			// Get their data from UA's enterprise data services
			EDSConfig edsConfig = new EDSConfig(DBUtil.getConfigProperties());
			EDSData edsData = new EDSService().getData(netid, edsConfig);
			logger.info("EDSData for {} = {}", netid, edsData);
			
			ArrayList<EDSCourse> courses = edsData.getCourses();
			
			if (courses.size() == 0 && user == null) {
				//
				// User doesn't exist and isn't taking any courses that use CC.
				return null; // TODO really need some indication that what we've detected is that
							 // that they're not registered for anything
			}
			
			if (courses.size() > 0  && user == null) {
				//
				// User has CC courses but isn't registered
				logger.info("unregistered user {} with courses {}; registering them", netid, courses);
				user = Database.getInstance().addNetidUser(netid, edsData.getGivenName(), edsData.getLastName());
				}
				
			//
			// Loop through any courses, trying to register user for each.  If addNetidUserToCourse returns false, they
			// were already registered for it.
			for (EDSCourse course: courses) {
				String description = String.format("%s (id=%d), section=%d", course.getMembership(), course.getCourseId(), course.getEncodedSection());
				String result;
				boolean added = Database.getInstance().addNetidStudentToCourse(user.getId(), course.getCourseId(), course.getEncodedSection());
				if (added)
					result = String.format("added %s to", netid);
				else
					result = String.format("%s already in", netid);
				
				logger.info("addNetidStudentToCourse: {} {}", result, description);
			}

			// Following code lifted from LoginServiceImpl.login(...)
			if (user != null) {
				// Set User object in server HttpSession so that other
				// servlets will know that the client is logged in
				HttpSession session = getThreadLocalRequest().getSession();
				session.setAttribute(SessionAttributeKeys.USER_KEY, user);
				
				// Set session timeout.
				int maxInactive = SESSION_TIMEOUT_IN_SECONDS;
				if (DEBUG_SESSION_TIMEOUTS) {
					// This is useful for testing automatic retry of RPC calls
					// that fail because of a server session timeout:
					// expires server sessions after 20 seconds of inactivity.
					maxInactive = 20;
				}
				session.setMaxInactiveInterval(maxInactive);
			}
			return user;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
