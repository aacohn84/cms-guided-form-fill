package controllers;

import java.util.List;
import java.util.Map;

import play.data.Form;
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

	public static Result updateFormVariables() {
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		CMSFormFactory.updateFormVariables("change_order", requestData);
		return ok();
	}
}
