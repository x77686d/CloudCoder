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

// First cut at a "logged out" page.  Didn't work and not sure why.  LoggedOutPage2 is what gets used. -- whm

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
 * Page to show after a logout.
 */
public class LoggedOutPage extends CloudCoderPage {
	private static final double LOGIN_VIEW_TOP_PX = 32.0;
	private static final double LOGIN_VIEW_WIDTH_PX = 340.0;
	private static final double LOGIN_VIEW_HEIGHT_PX = 480.0;
	private static final double LOGO_TOP_PX = 120.0;

	private class UI extends LayoutPanel implements SessionObserver {
		private InlineLabel pageTitleLabel;
		private ILoginView loginView;
		protected LoginSpec loginSpec;
		
		public UI() {
            VerticalPanel vp = new VerticalPanel();
            
            vp.setWidth("600px");
            
            vp.add(new HTML("You have logged out of CloudCoder"));
            vp.add(new HTML("Log out of WebAuth, too"));
            vp.add(new HTML("Log back in to CloudCoder"));

            /*
			Image cloudCoderLogoImage = new Image(GWT.getModuleBaseURL() + "images/CloudCoderLogo-med.png");
			add(cloudCoderLogoImage);
			setWidgetLeftWidth(cloudCoderLogoImage, LOGIN_VIEW_WIDTH_PX + 10.0, Unit.PX, 240.0, Unit.PX);
			setWidgetTopHeight(cloudCoderLogoImage, LOGO_TOP_PX, Unit.PX, 165.0, Unit.PX);
			
			pageTitleLabel = new InlineLabel("");
			pageTitleLabel.setStylePrimaryName("cc-pageTitle");
			add(pageTitleLabel);
			setWidgetLeftWidth(pageTitleLabel, 57.0, Unit.PX, 533.0, Unit.PX);
			setWidgetTopHeight(pageTitleLabel, 44.0, Unit.PX, 31.0, Unit.PX);
			
			InlineLabel welcomeLabel = new InlineLabel("Welcome to CloudCoder at");
			add(welcomeLabel);
			setWidgetLeftWidth(welcomeLabel, 57.0, Unit.PX, 313.0, Unit.PX);
			setWidgetTopHeight(welcomeLabel, 23.0, Unit.PX, 15.0, Unit.PX);
			*/
		}
		
		@Override
		public void activate(Session session, SubscriptionRegistrar subscriptionRegistrar) {

		}
	}

	public LoggedOutPage() {
		GWT.log("Creating LoggedOutPage");
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
