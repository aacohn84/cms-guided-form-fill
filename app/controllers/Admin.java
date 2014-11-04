package controllers;

import play.mvc.Result;

public class Admin extends SecureController {
	public static Result admin() {
		return ok(views.html.admin.render());
	}
}
