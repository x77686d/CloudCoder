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

package org.cloudcoder.app.client.view;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * UAz variant of SessionExpiredDialogBox.  If their CloudCoder session has expired the WebAuth session
 * is likely dead, too, so we just give a choice of returning to CloudCoder or being sure
 * they're out of WebAuth, too.
 * 
 * To keep things simple we leave in some methods that CloudCoderPage is expecting in the interface.
 * 
 * @author David Hovemeyer
 */
public class UAzSessionExpiredDialogBox extends DialogBox {
	private PasswordTextBox passwordBox;
	private Button loginButton;
	private HTML errorLabel;
	private Runnable loginButtonHandler;
	
	/**
	 * Constructor.
	 */
	public UAzSessionExpiredDialogBox() {
		setTitle("Session has timed out");
		setGlassEnabled(true);
		
		VerticalPanel vp = new VerticalPanel();
        vp.setWidth("600px");
        
        vp.add(new HTML("<p>Your CloudCoder session has timed out.</p>"));
        vp.add(new HTML("<a href='https://practice.cs.arizona.edu'>Return to CloudCoder<a>"));
        vp.add(new HTML("<a href='https://webauth.arizona.edu/webauth/logout'>Log out of WebAuth<a>"));
        add(vp);
	}

	/**
	 * Get the value entered in the password box.
	 * 
	 * @return value entered in the password box
	 */
	public String getPassword() {
		return passwordBox.getText();
	}
	
	/**
	 * Set an error message in the error label.
	 * 
	 * @param errorMessage the error message to set
	 */
	public void setError(String errorMessage) {
		errorLabel.setText(errorMessage);
	}
	
	/**
	 * Set a callback to run when the login button is clicked.
	 * 
	 * @param loginButtonHandler callback to run when login button is clicked
	 */
	public void setLoginButtonHandler(Runnable loginButtonHandler) {
		this.loginButtonHandler = loginButtonHandler;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#center()
	 */
	@Override
	public void center() {
		super.center();
	}
}
