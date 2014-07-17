package controllers;

import java.util.Date;

import models.User;
import play.mvc.Controller;
import play.mvc.Http.Session;

public class CMSSession extends Controller {
	
	public static void authenticate(User user) {
		Session s = session();
		s.put(AuthFields.USERNAME, user.getUsername());
		s.put(AuthFields.PERMISSION_LEVEL, user.getPermissionLevel());
		s.put(AuthFields.AUTH_TIME, new Date().toString());
	}
	
	public static class AuthFields {
		static final String USERNAME = "username";
		static final String PERMISSION_LEVEL = "permission";
		static final String AUTH_TIME = "auth_time";
	}
}
