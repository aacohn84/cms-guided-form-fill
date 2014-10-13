package controllers;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Date;
import java.util.Map.Entry;

import models.User;
import play.mvc.Controller;

public class CMSSession extends Controller {

	/**
	 * A catalogue of session keys used by this application.
	 */
	public static enum SessionKey {
		AUTH_TIME,
		PERMISSION_LEVEL,
		USERNAME;

		/**
		 * Provides the lower-case string representation of the enum.
		 */
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
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
	 * Clears all data from the current session.
	 */
	public static void clear() {
		session().clear();
	}

	/**
	 * Enter a value into the session.
	 */
	public static String get(SessionKey sessionKey) {
		return session().get(sessionKey.toString());
	}

	/**
	 * Returns true if the session has been authenticated.
	 */
	public static boolean isAuthenticated() {
		return isNotEmpty(get(SessionKey.USERNAME));
	}

	/**
	 * Prints the contents of the current session to the console.
	 */
	public static void print() {
		for (Entry<String, String> entry : session().entrySet()) {
			System.out.println("Key: " + entry.getKey());
			System.out.println("Val: " + entry.getValue());
			System.out.println();
		}
	}

	/**
	 * Retrieve a value from the session.
	 */
	public static String put(SessionKey sessionkey, String value) {
		return session().put(sessionkey.toString(), value);
	}

}
