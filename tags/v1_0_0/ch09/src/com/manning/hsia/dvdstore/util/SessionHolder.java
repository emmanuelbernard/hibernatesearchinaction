package com.manning.hsia.dvdstore.util;

import org.hibernate.search.FullTextSession;

public class SessionHolder {
	private static ThreadLocal<FullTextSession> sessionHolder = new ThreadLocal<FullTextSession>();
	
	public static FullTextSession getFullTextSession() {
		return sessionHolder.get();
	}
	
	public static void setFullTextSession(FullTextSession session) {
		sessionHolder.set(session);
	}
}
