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
			formData.getDecisionTree().makeDecision(root.id, null);
			formDataStore.setFormData(owner, formData);
		}
		DecisionTree decisionTree = formDataStore.getFormData(owner)
				.getDecisionTree();
		return decisionTree.getDecision(root.id);
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
		DecisionTree decisionMap = formData.getDecisionTree();

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
		DecisionTree decisionTree = formData.getDecisionTree();

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
		FilledFormFields fields = formData.getFilledFormFields();
		return PDFFormFiller.fillForm(formData.getForm(), fields, pdf);
	}
}
