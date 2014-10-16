package core;

import java.io.File;
import java.util.Map;

import play.Logger;
import core.forms.CMSForm;
import core.forms.ChangeOrderForm;
import core.pdf.PDFFormFiller;
import core.tree.Node;
import models.Decision;
import models.DecisionTree;
import models.FilledFormFields;
import models.FormData;
import models.FormDataStore;

public class CMSGuidedFormFill {
	private static File DECISIONS_FILE;

	public static void clearDecisions(String formName, String employee) {
		FormDataStore formDataStore = FormDataStore.getInstance();
		formDataStore.removeFormData(formName, employee);
	}

	public static void fillForm(String formName, String employee, File pdf) {
		// Get user's form data
		FormData formData = getFormData(formName, employee);

		// Convert FormData to PDF
		FilledFormFields fields = formData.getFilledFormFields();
		PDFFormFiller.fillForm(formData.getForm(), fields, pdf);
	}

	public static File getDecisionsFile() {
		return DECISIONS_FILE;
	}
	
	private static FormData getFormData(String formName, String employeeName) {
		FormDataStore formDataStore = FormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(formName, employeeName);
		return formData;
	}

	public static Decision getPreviousDecision(String formName,
			String employee, String idCurrentNode) {
		FormData formData = getFormData(formName, employee);
		DecisionTree decisionTree = formData.getDecisionTree();
		CMSForm form = formData.getForm();

		Decision currDecision = decisionTree.getDecision(idCurrentNode);
		Node prevNode = form.getNode(currDecision.previous.context.id);
		if (!prevNode.isVisible) {
			return getPreviousDecision(formName, employee, prevNode.id);
		}
		return decisionTree.getDecision(prevNode.id);
	}

	public static Decision makeDecision(String formName, String employee,
			String idCurrentNode, Map<String, String> requestData) {
		FormData formData = getFormData(formName, employee);

		Logger.info("Making Decision for (" + formData.getEmployeeName() + ", "
				+ formData.getForm().getName() + ")" + " at node: "
				+ idCurrentNode);

		DecisionTree decisionTree = formData.getDecisionTree();
		Decision decision = decisionTree.makeDecision(idCurrentNode,
				requestData);
		Node nextContext = decision.next.context;
		while (!decision.context.isTerminal() && !nextContext.isVisible) {
			Logger.info("Node " + nextContext.id + " is not visible, so a "
					+ "Decision will be made for it without further input.");
			decision = decisionTree.makeDecision(nextContext.id, requestData);
			nextContext = decision.next.context;
		}
		return decision.next;
	}

	public static void saveForm(String formName, String employee) {
		FormData formData = getFormData(formName, employee);
		FormDataStore.getInstance().saveFormData(formData, employee);
	}

	public static void setDecisionsFile(File decisionsFile) {
		DECISIONS_FILE = decisionsFile;
	}

	/**
	 * Starts the form-filling process for the specified employee.
	 * 
	 * @param employeeName
	 *            - the logged-in user who owns the data created and stored
	 *            during this form-filling session.
	 * @return the Decision associated with the root node of the decision tree.
	 *         If a Decision doesn't exist yet, it will be created.
	 */
	public static Decision startOrContinueForm(String formName,
			String employeeName, int employeeId) {
		FormDataStore formDataStore = FormDataStore.getInstance();
		ChangeOrderForm form = ChangeOrderForm.getInstance();
		Node root = form.getRoot();
		if (!formDataStore.containsEntry(formName, employeeName)) {
			Logger.info("No FormData found for " + "(" + formName + ", "
					+ employeeName + "): new FormData will be created.");
			FormData formData = new FormData(employeeName, employeeId, form);
			formData.getDecisionTree().makeDecision(root.id, null);
			formDataStore.saveFormData(formData, employeeName);
		}
		DecisionTree decisionTree = formDataStore.getFormData(formName,
				employeeName).getDecisionTree();
		return decisionTree.getDecision(root.id);
	}
}
