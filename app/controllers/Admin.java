package controllers;

import java.util.List;

import play.mvc.Result;
import core.forms.CMSForm;
import core.forms.CMSFormFactory;
import core.forms.FormVariable;

public class Admin extends SecureController {
	public static Result admin() {
		return ok(views.html.admin.render());
	}

	public static Result formVariables() {
		CMSForm form = CMSFormFactory.getForm("change_order");
		List<FormVariable> formVars = form.getFormVariables();
		return ok(views.html.formVariables.render(formVars));
	}

	public static Result resetChangeOrder() {
		CMSFormFactory.resetForm("change_order");
		return ok();
	}
}
