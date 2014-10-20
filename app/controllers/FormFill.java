package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import models.Decision;
import models.EmployeeHistoryEntry;
import play.Logger;
import play.data.Form;
import play.mvc.Result;
import util.FileDeletionHandler;
import views.html.loadPrevious;
import views.html.questionnaire.backdrop;
import core.CMSGuidedFormFill;

public class FormFill extends SecureController {

	public static class RequestParams {
		public static final String CURRENT_NODE = "currentNode";
	}

	/*
	 * Starts a new form instance for the employee.
	 */
	public static Result startNewForm() {
		String formName = "change_order";
		Decision firstDecision = CMSGuidedFormFill.startNewForm(formName,
				CMSSession.getEmployeeName(), CMSSession.getEmployeeId());
		return ok(backdrop.render(firstDecision));
	}

	public static Result continueCurrentForm() {
		String formName = "change_order";
		Decision mostRecentDecision = CMSGuidedFormFill.continueForm(formName,
				CMSSession.getEmployeeName());
		if (mostRecentDecision == null) {
			flash().put("noCurrentForm", "");
			return redirect("/forms");
		}
		return ok(backdrop.render(mostRecentDecision));
	}

	public static Result loadPrevious() {
		return ok("not implemented");
	}

	public static Result getEmployeeHistory() {
		String formName = "change_order";
		Integer employeeId = CMSSession.getEmployeeId();
		List<EmployeeHistoryEntry> employeeHistory = CMSGuidedFormFill
				.getEmployeeHistory(formName, employeeId);
		return ok(loadPrevious.render(employeeHistory));
	}

	/*
	 * Provide filled PDF for viewing/editing/printing.
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
	 * Save decision and provide next node in the form based on user input.
	 */
	public static Result next() {
		// get current node
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		String idCurrentNode = requestData.get(RequestParams.CURRENT_NODE);
		String formName = "change_order";
		String employeeName = CMSSession.getEmployeeName();

		Decision nextDecision = CMSGuidedFormFill.makeDecision(formName,
				employeeName, idCurrentNode, requestData);

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
