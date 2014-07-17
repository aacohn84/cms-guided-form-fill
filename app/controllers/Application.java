package controllers;

import models.User;
import models.data.AuthDataStore;
import models.data.MySQLAuthDataStore;
import play.*;
import play.mvc.*;
import static play.data.Form.form;
import play.data.Form;
import views.html.*;
import play.mvc.Security.Authenticated;
import play.mvc.Security.Authenticator;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    final static Form<User> loginForm = form(User.class);
    
    /*
     * Authenticate a user who is attempting to log in.
     */
    public static Result authenticate() {
    	Form<User> filledLoginForm = loginForm.bindFromRequest();
    	if (filledLoginForm.hasErrors()) {
    		return badRequest(login.render(filledLoginForm));
    	}
    	AuthDataStore authDataStore = new MySQLAuthDataStore();
    	User user = filledLoginForm.get();
    	String username = user.getUsername();
    	if (authDataStore.credentialsAreValid(username, user.getPassword())) {
    		user.setPermissionLevel(authDataStore.getPermissionLevel(username));
    		CMSSession.authenticate(user);
    		return redirect("/Main");
    	}
    	filledLoginForm.reject("Something wasn't right with your username or password.");
    	return badRequest(login.render(filledLoginForm));
    }
    
    /*
     * Display the landing page. (requires authentication)
     */
    @Authenticated(Authenticator.class)
    public static Result forms() {
    	return ok("Authentication Successful.");
    }
}
