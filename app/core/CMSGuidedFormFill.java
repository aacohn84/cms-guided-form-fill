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
import models.InMemoryFormDataStore;

public class CMSGuidedFormFill {
	public static void clearDecisions(String owner) {
		InMemoryFormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		if (formDataStore.containsUsername(owner)) {
			formDataStore.removeFormData(owner);
		}
	}

	public static void fillForm(String owner, File pdf) {
		// Get user's form data
		FormData formData = getFormData(owner);

		// Convert FormData to PDF
		fillPdfWithFormData(formData, pdf);
	}

	public static Decision getPreviousDecision(String owner,
			String idCurrentNode) {
		FormData formData = getFormData(owner);
		DecisionTree decisionTree = formData.getDecisionTree();
		CMSForm form = formData.getForm();

		Decision currDecision = decisionTree.getDecision(idCurrentNode);
		Node prevNode = form.getNode(currDecision.previous.context.id);
		if (!prevNode.isVisible) {
			return getPreviousDecision(owner, prevNode.id);
		}
		return decisionTree.getDecision(prevNode.id);
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

		FormData formData = getFormData(owner);
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

	public static void saveForm(String owner) {
		// retrieve the user's FormData
		FormData formData = getFormData(owner);
		
		// if the FormData has a database row associated with it
		if (formData.getRowId() != null) {
			// update the row
			
		}
		// else
		//     create a new row
		// set FormData's id to the id of the new row
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
		InMemoryFormDataStore formDataStore = InMemoryFormDataStore.getInstance();
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

	private static File fillPdfWithFormData(FormData formData, File pdf) {
		FilledFormFields fields = formData.getFilledFormFields();
		return PDFFormFiller.fillForm(formData.getForm(), fields, pdf);
	}

	private static FormData getFormData(String owner) {
		InMemoryFormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);
		return formData;
	}
}
