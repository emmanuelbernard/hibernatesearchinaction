package com.manning.hsia.dvdstore.util;

import org.hibernate.Session;

public class SessionHolder {
	private static ThreadLocal<Session> sessionHolder = new ThreadLocal<Session>();
	
	public static Session getSession() {
		return sessionHolder.get();
	}
	
	public static void setSession(Session session) {
		sessionHolder.set(session);
	}
}
