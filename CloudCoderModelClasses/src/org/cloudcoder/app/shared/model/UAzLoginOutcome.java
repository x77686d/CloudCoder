package org.cloudcoder.app.shared.model;

import java.io.Serializable;


public class UAzLoginOutcome implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private User user;
	private String message;
	
	public UAzLoginOutcome() {}
	
	public UAzLoginOutcome(User user, String message) {
		this.user = user;
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
