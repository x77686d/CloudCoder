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

package org.cloudcoder.app.client.page;

import java.util.Arrays;

import org.cloudcoder.app.client.model.PageId;
import org.cloudcoder.app.client.model.PageStack;
import org.cloudcoder.app.client.model.Session;
import org.cloudcoder.app.client.model.StatusMessage;
import org.cloudcoder.app.client.rpc.RPC;
import org.cloudcoder.app.client.view.StatusMessageView;
import org.cloudcoder.app.shared.util.SubscriptionRegistrar;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page to display UAz login failure messages.
 * 
 * @author David Hovemeyer
 * @author William Mitchell (Hacked up from InitErrorPage.java)
 */
public class UAzLoginFailurePage extends CloudCoderPage {
	private String message;
	
	public UAzLoginFailurePage(String message) {
		this.message = message;
	}
	
	private class UI extends Composite {
		private CellList<String> errorList;
		private StatusMessageView statusMessageView;

		public UI() {
			LayoutPanel panel = new LayoutPanel();
			
			VerticalPanel vp = new VerticalPanel();
            vp.setWidth("600px");
            
            vp.add(new HTML("<span class=cc-pageTitle>CloudCoder login failed:</span>"));
            vp.add(new HTML("<blockquote>" + message + "</blockquote>"));
            vp.add(new HTML("<a href='https://webauth.arizona.edu/webauth/logout'>Log out of WebAuth<a>"));
            vp.add(new HTML("<a href='https://practice.cs.arizona.edu'>Try again to login to CloudCoder<a>"));
            panel.add(vp);
			
			initWidget(panel);
		}

		public void activate(final Session session, final SubscriptionRegistrar subscriptionRegistrar) {
			statusMessageView.activate(session, subscriptionRegistrar);
			
			RPC.loginService.getInitErrorList(new AsyncCallback<String[]>() {
				@Override
				public void onSuccess(String[] result) {
					onLoadInitErrorList(result);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					session.add(StatusMessage.error(
							"Could not load init error list (check webapp log): " +
							caught.getMessage()));
				}
			});
		}

		protected void onLoadInitErrorList(String[] result) {
			errorList.setRowCount(result.length);
			errorList.setRowData(0, Arrays.asList(result));
		}
	}

	@Override
	public void createWidget() {
		setWidget(new UI());
	}
	
	@Override
	public Class<?>[] getRequiredPageObjects() {
		// No page objects are required
		return new Class<?>[0];
	}

	@Override
	public void activate() {
		((UI)getWidget()).activate(getSession(), getSubscriptionRegistrar());
	}
	
	@Override
	public PageId getPageId() {
		return PageId.INIT_ERROR;
	}

	@Override
	public void initDefaultPageStack(PageStack pageStack) {
		throw new IllegalStateException("Not an activity");
	}
}
