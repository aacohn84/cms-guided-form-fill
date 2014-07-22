package controllers;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Date;

import models.User;
import play.mvc.Controller;

public class CMSSession extends Controller {
	
	/**
	 * Enter a value into the session.
	 */
	public static String get(SessionKey sessionKey) {
		return session().get(sessionKey.toString());
	}
	
	/**
	 * Retrieve a value from the session.
	 */
	public static String put(SessionKey sessionkey, String value) {
		return session().put(sessionkey.toString(), value);
	}
	
	/**
	 * Enter the given user's session data and note the time of authentication.
	 */
	public static void authenticate(User user) {
		put(SessionKey.USERNAME, user.getUsername());
		put(SessionKey.PERMISSION_LEVEL, user.getPermissionLevel());
		put(SessionKey.AUTH_TIME, new Date().toString());
	}

	/**
	 * Returns true if the session has been authenticated.
	 */
	public static boolean isAuthenticated() {
		return isNotEmpty(get(SessionKey.USERNAME));
	}	
	
	/**
	 * Clears all data from the current session.
	 */
	public static void clear() {
		session().clear();
	}

	/**
	 * A catalogue of session keys used by this application.
	 */
	public static enum SessionKey {
		// Authentication
		USERNAME,
		PERMISSION_LEVEL,
		AUTH_TIME;
		
		/**
		 * Provides the lower-case string representation of the enum.
		 */
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}


}
