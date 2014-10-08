package core;

import java.io.File;
import java.util.Map;

import core.forms.CMSForm;
import core.forms.ChangeOrderForm;
import core.pdf.PDFFormFiller;
import core.tree.Node;
import models.Decision;
import models.DecisionTree;
import models.FilledFormFields;
import models.FormData;
import models.FormDataStore;
import models.InMemoryFormDataStore;

public class CMSGuidedFormFill {
	public static void clearDecisions(String owner) {
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		if (formDataStore.containsUsername(owner)) {
			formDataStore.removeFormData(owner);
		}
	}

	/**
	 * Starts the form-filling process for the specified owner.
	 * 
	 * @param owner
	 *            - the logged-in user who owns the data created and stored
	 *            during this form-filling session.
	 * @return the Decision associated with the root node of the decision tree.
	 *         If a Decision doesn't exist yet, it will be created.
	 */
	public static Decision startOrContinueForm(String owner) {
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		ChangeOrderForm form = ChangeOrderForm.getInstance();
		Node root = form.getRoot();
		if (!formDataStore.containsUsername(owner)) {
			FormData formData = new FormData(owner, form);
			Decision firstDecision = new Decision().setContext(root);
			formData.decisionTree.putDecision(firstDecision);
			formDataStore.setFormData(owner, formData);
		}
		DecisionTree decisionMap = formDataStore.getFormData(owner).decisionTree;
		return decisionMap.getDecision(root.id);
	}

	public static void fillForm(String owner, File pdf) {
		// Get user's form data
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);

		// Convert FormData to PDF
		fillPdfWithFormData(formData, pdf);
	}

	public static Decision getPreviousDecision(String owner,
			String idCurrentNode) {
		/*
		 * Retrieve the owner's form data first. We won't bother continuing if
		 * this raises an exception.
		 */
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);
		DecisionTree decisionMap = formData.decisionTree;

		CMSForm form = ChangeOrderForm.getInstance();
		Decision currDecision = decisionMap.getDecision(idCurrentNode);
		Node prevNode = form.getNode(currDecision.previous.context.id);
		if (!prevNode.isVisible) {
			return getPreviousDecision(owner, prevNode.id);
		}
		return decisionMap.getDecision(prevNode.id);
	}

	/**
	 * Saves the decision and returns the next node in the form.
	 * 
	 * @param idCurrentNode
	 *            - id of the node
	 * @param requestData
	 *            - a mapping of field names to input values.
	 */
	public static Decision makeDecision(String owner, String idCurrentNode,
			Map<String, String> requestData) {

		// retrieve the owner's FormData
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);
		DecisionTree decisionTree = formData.decisionTree;

		Decision decision = decisionTree.makeDecision(idCurrentNode,
				requestData);

		/*
		 * Return next decision if context is visible, else continue making
		 * decisions.
		 */
		if (!decision.next.context.isVisible) {
			return makeDecision(owner, decision.next.context.id, requestData);
		}
		return decision.next;
	}

	public static void saveForm() {
		// Save form data to the database (create or update)
	}

	private static File fillPdfWithFormData(FormData formData, File pdf) {
		// fill fields along the path the user took through the decision tree
		DecisionTree decisions = formData.decisionTree;
		FilledFormFields fields = new FilledFormFields();
		for (Decision decision : decisions) {
			Node context = decision.context;
			if (context.isOutputNode) {
				context.fillFormFields(decision.serializedInput, fields);
			}
		}
		CMSForm form = ChangeOrderForm.getInstance();
		PDFFormFiller formFiller = new PDFFormFiller();
		return formFiller.fillForm(form, fields, pdf);
	}
}
