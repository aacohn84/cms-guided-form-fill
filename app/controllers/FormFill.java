package controllers;

import java.io.File;
import java.util.Map;

import models.data.Decision;
import play.data.Form;
import play.mvc.Result;
import util.FileDeletionHandler;
import views.html.questionnaire.backdrop;
import core.CMSGuidedFormFill;

public class FormFill extends SecureController {

	public static class RequestParams {
		public static final String CURRENT_NODE = "currentNode";
	}

	/*
	 * Deletes the user's saved decisions.
	 */
	public static Result clearSavedChoices() {
		String username = getUsername();
		CMSGuidedFormFill.clearDecisions(username);
		flash().put("clearSavedChoices", "");
		return redirect(routes.Application.forms());
	}
	
	/*
	 * Provide root node of form.
	 */
	public static Result startOrContinueForm() {
		Decision firstDecision = CMSGuidedFormFill
				.startOrContinueForm(getUsername());
		return ok(backdrop.render(firstDecision));
	}

	/*
	 * Provide filled PDF for viewing/editing/printing
	 */
	public static Result getFormOutput() {
		String username = getUsername();
		File filledForm = CMSGuidedFormFill.getFormOutput(username);
		Status result = ok(filledForm, true);
		FileDeletionHandler.deleteFile(filledForm);
		return result;
	}

	/*
	 * Save decision and provide next node in active path of form.
	 */
	public static Result next() {
		// get current node
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		String idCurrentNode = requestData.get(RequestParams.CURRENT_NODE);
		String username = getUsername();

		Decision nextDecision = CMSGuidedFormFill.makeDecision(username,
				idCurrentNode, requestData);

		return ok(backdrop.render(nextDecision));
	}

	/*
	 * Provide previous node in active path of form.
	 */
	public static Result prev() {
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		String idCurrentNode = requestData.get(RequestParams.CURRENT_NODE);
		String username = getUsername();

		Decision previousDecision = CMSGuidedFormFill.getPreviousDecision(
				username, idCurrentNode);

		return ok(backdrop.render(previousDecision));
	}
}
