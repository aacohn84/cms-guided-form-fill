package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.data.Decision;
import models.data.DecisionMap;
import models.data.FilledFormFields;
import models.data.FormData;
import models.data.FormDataStore;
import models.data.InMemoryFormDataStore;
import models.forms.CMSForm;
import models.forms.ChangeOrderForm;
import models.pdf.PDFFormFiller;
import models.tree.Node;

public class CMSGuidedFormFill {
	public static Decision getFirstDecision(String owner) {
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		Node root = ChangeOrderForm.getInstance().getRoot();
		if (!formDataStore.containsUsername(owner)) {
			FormData formData = new FormData(owner);
			Decision firstDecision = new Decision().setContext(root);
			formData.decisionMap.putDecision(firstDecision);
			formDataStore.setFormData(owner, formData);
		}
		DecisionMap decisionMap = formDataStore.getFormData(owner).decisionMap;
		return decisionMap.getDecision(root.id);
	}

	public static File getFormOutput(String owner) {
		// Get user's form data
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);

		// Convert FormData to PDF
		File filledPdf = getFilledPdf(formData);

		return filledPdf;
	}

	public static Decision getPreviousDecision(String owner,
			String idCurrentNode) {
		/*
		 * Retrieve the owner's form data first. We won't bother continuing if
		 * this raises an exception.
		 */
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);
		DecisionMap decisionMap = formData.decisionMap;

		CMSForm form = ChangeOrderForm.getInstance();
		Decision currDecision = decisionMap.getDecision(idCurrentNode);
		Node prevNode = form.getNode(currDecision.previous.context.id);
		if (!prevNode.isVisible) {
			return getPreviousDecision(owner, prevNode.id);
		}
		return decisionMap.getDecision(prevNode.id);
	}

	/**
	 * Returns a list of decisions corresponding to the owner's path through the
	 * decision tree. Only decisions that result in form output are listed.
	 */
	public static List<Decision> getTestOutput(String owner) {
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);
		DecisionMap decisions = formData.decisionMap;

		List<Decision> formOutput = new ArrayList<>();
		for (Decision currDecision : decisions) {
			Node currNode = currDecision.context;
			if (currNode.isOutputNode) {
				formOutput.add(currDecision);
			}
		}
		return formOutput;
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
		/*
		 * Retrieve the owner's form data first, since there's no point in doing
		 * anything else if the form data doesn't exist.
		 */
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);
		DecisionMap decisionMap = formData.decisionMap;

		// retrieve the current node
		CMSForm form = ChangeOrderForm.getInstance();
		Node currentNode = form.getNode(idCurrentNode);

		// TODO: validate user input

		Decision currDecision = retrieveOrCreateCurrentDecision(idCurrentNode,
				decisionMap, currentNode);
		String rawInput = currentNode.serializeInput(requestData);
		currDecision.setRawInput(rawInput);

		// link current decision and next decision to each other
		String idNextNode = currentNode.getIdNextNode(requestData);
		Node nextNode = form.getNode(idNextNode);
		Decision nextDecision = retrieveOrCreateNextDecision(decisionMap,
				nextNode);
		nextDecision.setPrevious(currDecision);
		currDecision.setNext(nextDecision);

		// save current and next decisions
		decisionMap.putDecision(currDecision);
		decisionMap.putDecision(nextDecision);

		// fill/update form fields
		FilledFormFields filledFormFields = formData.filledFormFields;
		if (currentNode.isOutputNode) {
			currentNode.fillFormFields(currDecision.rawInput, filledFormFields);
		}

		/*
		 * Return decision associated with next node. If next node not visible,
		 * make decision for it and repeat.
		 */
		if (!nextNode.isVisible) {
			return makeDecision(owner, currDecision.next.context.id, requestData);
		}
		return currDecision.next;
	}

	private static File getFilledPdf(FormData formData) {
		// fill fields along the path the user took through the decision tree
		DecisionMap decisions = formData.decisionMap;
		FilledFormFields fields = new FilledFormFields();
		for (Decision decision : decisions) {
			Node context = decision.context;
			if (context.isOutputNode) {
				context.fillFormFields(decision.rawInput, fields);
			}
		}
		CMSForm form = ChangeOrderForm.getInstance();
		PDFFormFiller formFiller = new PDFFormFiller();
		return formFiller.fillForm(form, fields);
	}

	private static Decision retrieveOrCreateCurrentDecision(
			String idCurrentNode, DecisionMap decisionMap, Node currentNode) {
		Decision decision = decisionMap.getDecision(idCurrentNode);
		if (decision == null) {
			decision = new Decision().setContext(currentNode);
		}
		return decision;
	}

	private static Decision retrieveOrCreateNextDecision(DecisionMap decisions,
			Node nextNode) {
		Decision nextDecision = decisions.getDecision(nextNode.id);
		if (nextDecision == null) {
			nextDecision = new Decision().setContext(nextNode);
		}
		return nextDecision;
	}
}
