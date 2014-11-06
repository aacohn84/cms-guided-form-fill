package core;

import java.io.File;
import java.util.List;
import java.util.Map;

import models.CMSDB;
import models.Decision;
import models.DecisionTree;
import models.EmployeeHistoryEntry;
import models.FilledFormFields;
import models.FormData;
import models.FormDataStore;
import play.Logger;
import core.forms.CMSForm;
import core.forms.CMSFormFactory;
import core.forms.ChangeOrderForm;
import core.pdf.PDFFormFiller;
import core.tree.Node;

public class CMSGuidedFormFill {
	private static File FORM_DATA_FILE;

	public static void clearFormData(String formName, String employee) {
		FormDataStore formDataStore = FormDataStore.getInstance();
		formDataStore.removeFormData(formName, employee);
	}

	/**
	 * Fills the form fields of the given PDF file.
	 *
	 * @param formName
	 *            - name of the form being filled.
	 * @param employee
	 *            - name of the employee who owns the form data.
	 * @param writableFile
	 *            - a file that can be created or overwritten with a filled PDF.
	 */
	public static void fillForm(String formName, String employee,
			File writableFile) {
		FormData formData = getFormData(formName, employee);

		// Convert FormData to PDF
		FilledFormFields fields = formData.getFilledFormFields();
		PDFFormFiller.fillForm(formData.getForm(), fields, writableFile);
	}

	public static File getFormDataFile() {
		return FORM_DATA_FILE;
	}

	public static void setFormDataFile(File formDataFile) {
		FORM_DATA_FILE = formDataFile;
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

	/**
	 * Start a new instance of the chosen form (wipes most recently accessed
	 * FormData from memory).
	 *
	 * @param formName
	 *            - name of the form to start
	 * @param employeeName
	 *            - name of the employee starting the form
	 * @param employeeId
	 *            - database ID of the employee starting the form
	 * @return the first {@link Decision} in the {@link DecisionTree} for this
	 *         form.
	 */
	public static Decision startNewForm(String formName, String employeeName,
			int employeeId) {
		FormDataStore formDataStore = FormDataStore.getInstance();
		formDataStore.removeFormData(formName, employeeName);

		CMSForm form = CMSFormFactory.getForm(formName);
		Node root = form.getRoot();
		FormData formData = new FormData(employeeName, employeeId, form);

		DecisionTree decisionTree = formData.getDecisionTree();
		decisionTree.makeDecision(root.id, null);
		formDataStore.saveFormData(formData, employeeName);

		return decisionTree.getFirstDecision();
	}

	/**
	 * Allows the employee to pick up where they left off in their most recently
	 * accessed form.
	 *
	 * @param formName
	 *            - name of the form to continue.
	 * @param employeeName
	 *            - name of the employee.
	 * @return the next Decision to be made by the employee, if one exists. If
	 *         the form is complete, then the last Decision. Otherwise,
	 *         <code>null</code>.
	 */
	public static Decision continueForm(String formName, String employeeName) {
		FormDataStore formDataStore = FormDataStore.getInstance();
		if (formDataStore.containsEntry(formName, employeeName)) {
			FormData formData = formDataStore.getFormData(formName,
					employeeName);
			DecisionTree decisionTree = formData.getDecisionTree();
			return decisionTree.getLastDecision();
		}
		return null;
	}

	private static FormData getFormData(String formName, String employeeName) {
		FormDataStore formDataStore = FormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(formName, employeeName);
		return formData;
	}

	public static List<EmployeeHistoryEntry> getEmployeeHistory(
			String formName, int employeeId) {
		return CMSDB.getEmployeeHistory(employeeId, formName);
	}

	public static Decision loadPreviouslyCompletedForm(String formName,
			String employeeName, int employeeId, int rowId) {
		FormDataStore formDataStore = FormDataStore.getInstance();
		CMSForm form = CMSFormFactory.getForm(formName);
		formDataStore.loadFormData(form, employeeName, employeeId, rowId);
		return continueForm(formName, employeeName);
	}

	public static void setFormVariables(String formName, Map<String, String> requestData) {
		CMSFormFactory.updateFormVariables(formName, requestData);
	}
}
