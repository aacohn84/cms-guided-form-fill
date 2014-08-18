package controllers;

import java.io.File;
import java.util.Map;

import models.data.Decision;
import play.data.Form;
import play.mvc.Result;
import views.html.questionnaire.backdrop;
import core.CMSGuidedFormFill;

public class FormFill extends SecureController {

	public static class RequestParams {
		public static final String CURRENT_NODE = "currentNode";
	}

	/*
	 * Provides the root node of the requested form.
	 */
	public static Result getForm() {
		Decision firstDecision = CMSGuidedFormFill
				.getFirstDecision(getUsername());
		return ok(backdrop.render(firstDecision));
	}

	public static Result getFormOutput() {
		String username = getUsername();
		/*List<Decision> formOutput = CMSGuidedFormFill.getTestOutput(username);

		return ok(views.html.questionnaire.output.render(formOutput));*/
		File filledForm = CMSGuidedFormFill.getFormOutput(username);
		return ok(filledForm, true);
	}

	/*
	 * Saves the decision and provides the next node of the change order form.
	 */
	public static Result getNextNode() {
		// get current node
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		String idCurrentNode = requestData.get(RequestParams.CURRENT_NODE);
		String username = getUsername();

		Decision nextDecision = CMSGuidedFormFill.makeDecision(username,
				idCurrentNode, requestData);

		return ok(backdrop.render(nextDecision));
	}
	
}
