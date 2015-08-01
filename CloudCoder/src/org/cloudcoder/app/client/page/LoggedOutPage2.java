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

package org.cloudcoder.app.client.page;

import org.cloudcoder.app.client.CloudCoder;
import org.cloudcoder.app.client.model.PageId;
import org.cloudcoder.app.client.model.PageStack;
import org.cloudcoder.app.client.model.Session;
import org.cloudcoder.app.client.rpc.RPC;
import org.cloudcoder.app.client.view.ILoginView;
import org.cloudcoder.app.client.view.PreauthorizedUserLoginView;
import org.cloudcoder.app.client.view.UsernamePasswordLoginView;
import org.cloudcoder.app.shared.model.LoginSpec;
import org.cloudcoder.app.shared.model.User;
import org.cloudcoder.app.shared.util.SubscriptionRegistrar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Login page.
 */
public class LoggedOutPage2 extends CloudCoderPage {
	
	/**
	 * UI class for LoginPage.
	 */
	private class UI extends LayoutPanel implements SessionObserver {
		private InlineLabel pageTitleLabel;
		
		public UI() {
			/*
			pageTitleLabel = new InlineLabel("");
			pageTitleLabel.setStylePrimaryName("cc-pageTitle");
			add(pageTitleLabel);
			setWidgetLeftWidth(pageTitleLabel, 57.0, Unit.PX, 533.0, Unit.PX);
			setWidgetTopHeight(pageTitleLabel, 44.0, Unit.PX, 31.0, Unit.PX);
			InlineLabel loggedOutLabel = new InlineLabel("You have logged out of CloudCoder (2)");
			loggedOutLabel.setStylePrimaryName("cc-pageTitle");
			*/
			
			VerticalPanel vp = new VerticalPanel();
            vp.setWidth("600px");
            
            vp.add(new HTML("<span class=cc-pageTitle>You have logged out of CloudCoder</span>"));
            vp.add(new HTML("<a href='https://webauth.arizona.edu/webauth/logout'>Log out of WebAuth, too<a>"));
            vp.add(new HTML("<a href='https://practice.cs.arizona.edu'>Login to CloudCoder Again<a>"));
            add(vp);

		}
		
		@Override
		public void activate(Session session, SubscriptionRegistrar subscriptionRegistrar) {
			RPC.loginService.getLoginSpec(new AsyncCallback<LoginSpec>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Failure to get LoginSpec", caught);
				}
				
				public void onSuccess(LoginSpec result) {
				}
			});
		}

	}

	/**
	 * Default constructor.
	 * Will take the user to the {@link CoursesAndProblemsPage2}
	 * upon a successful login.
	 */
	public LoggedOutPage2() {
		GWT.log("Creating LoginPage, fragment is " + Window.Location.getHash());
	}
	
	/**
	 * Set the {@link PageId} that was specified in the original URL.
	 * The login page should try to navigate to it on a successful login.
	 * 
	 * @param linkPageId the linkPageId to set
	 */
	public void setLinkPageId(PageId linkPageId) {
	}
	
	/**
	 * Set the page parameters that were specified in the original URL.
	 * 
	 * @param linkPageParams the linkPageParams to set
	 */
	public void setLinkPageParams(String linkPageParams) {
	}

	@Override
	public void createWidget() {
		setWidget(new UI());
	}
	
	@Override
	public Class<?>[] getRequiredPageObjects() {
		// This page does not require any page objects
		return new Class<?>[0];
	}
	
	@Override
	public void activate() {
		((UI)getWidget()).activate(getSession(), getSubscriptionRegistrar());
	}
	
	@Override
	public PageId getPageId() {
		return PageId.LOGGEDOUT;
	}
	
	@Override
	public void initDefaultPageStack(PageStack pageStack) {
		throw new IllegalStateException("Not an activity");
	}
}
