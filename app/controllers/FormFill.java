package controllers;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import models.Decision;
import play.Logger;
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
		String username = CMSSession.getEmployeeName();
		String formName = "change_order";
		CMSGuidedFormFill.clearDecisions(formName, username);
		flash().put("clearSavedChoices", "");
		return redirect(routes.Application.forms());
	}

	/*
	 * Provide root node of form.
	 */
	public static Result startOrContinueForm() {
		String formName = "change_order";
		Decision firstDecision = CMSGuidedFormFill.startOrContinueForm(
				formName, CMSSession.getEmployeeName(),
				CMSSession.getEmployeeId());
		return ok(backdrop.render(firstDecision));
	}

	/*
	 * Provide filled PDF for viewing/editing/printing
	 */
	public static Result getFilledForm() {
		Status result;
		try {
			String formName = "change_order";
			String username = CMSSession.getEmployeeName();
			File pdf = File.createTempFile("Change_Order_Form_Filled", ".pdf");
			CMSGuidedFormFill.fillForm(formName, username, pdf);

			result = ok(pdf, true);

			FileDeletionHandler.deleteFile(pdf);
		} catch (IOException e) {
			Logger.error("Couldn't create new temp file.");
			Logger.error(e.getMessage(), e);
			result = internalServerError();
		}
		return result;
	}

	/*
	 * Save decision and provide next node in active path of form.
	 */
	public static Result next() {
		// get current node
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		String idCurrentNode = requestData.get(RequestParams.CURRENT_NODE);
		String formName = "change_order";
		String username = CMSSession.getEmployeeName();

		Decision nextDecision = CMSGuidedFormFill.makeDecision(formName,
				username, idCurrentNode, requestData);

		return ok(backdrop.render(nextDecision));
	}

	/*
	 * Provide previous node in active path of form.
	 */
	public static Result prev() {
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		String idCurrentNode = requestData.get(RequestParams.CURRENT_NODE);
		String formName = "change_order";
		String username = CMSSession.getEmployeeName();

		Decision previousDecision = CMSGuidedFormFill.getPreviousDecision(
				formName, username, idCurrentNode);

		return ok(backdrop.render(previousDecision));
	}

	public static Result saveAndExit() {
		String formName = "change_order";
		CMSGuidedFormFill.saveForm(formName, CMSSession.getEmployeeName());
		return redirect("/forms");
	}
}
