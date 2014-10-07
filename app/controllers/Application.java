package controllers;

import static play.data.Form.form;
import models.AuthStore;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import play.mvc.Security.Authenticator;
import views.html.login;

public class Application extends Controller {

	final static Form<User> loginForm = form(User.class);

    /*
     * Authenticate a user who is attempting to log in.
     */
    public static Result authenticate() {
    	Form<User> filledLoginForm = loginForm.bindFromRequest();
    	if (filledLoginForm.hasErrors()) {
    		return badRequest(login.render(filledLoginForm));
    	}
    	AuthStore authStore = new AuthStore();
    	User user = filledLoginForm.get();
    	String username = user.getUsername();
    	if (authStore.credentialsAreValid(username, user.getPassword())) {
    		user.setPermissionLevel(authStore.getPermissionLevel(username));
    		CMSSession.authenticate(user);
    		return redirect("/forms");
    	}
    	filledLoginForm.reject("Something wasn't right with your username or password.");
    	return badRequest(login.render(filledLoginForm));
    }

    /*
     * Display the landing page. (requires authentication)
     */
    @Authenticated(Authenticator.class)
    public static Result forms() {
    	return ok(views.html.forms.render());
    }
    
    /*
	 * Serve the forms page for a logged-in user; login page for a user who is
	 * not logged in.
	 */
    public static Result index() {
    	if (CMSSession.isAuthenticated()) {
    		return redirect("/forms");
    	}
        return ok(login.render(null));
    }
    
    public static Result logout() {
    	CMSSession.clear();
    	return ok(login.render(null));
    }
}
