package controllers;

import models.forms.CMSForm;
import models.forms.ChangeOrderForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.questionnaire.backdrop;

public class FormFill extends Controller {

	public static class RequestParams {
		public static final String CURRENT_NODE = "currentNode";
	}

	/*
	 * Provides the root node of the requested form.
	 */
	public static Result getForm() {
		CMSForm form = ChangeOrderForm.getInstance();
		return ok(backdrop.render(form.getRoot()));
	}

}
