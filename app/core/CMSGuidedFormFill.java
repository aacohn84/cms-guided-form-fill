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

		// retrieve the current node
		CMSForm form = ChangeOrderForm.getInstance();
		Node currentNode = form.getNode(idCurrentNode);

		// TODO: validate user input

		// create a decision based on the data available
		FilledFormFields filledFormFields = formData.filledFormFields;
		Decision decision = currentNode.createDecision(requestData,
				filledFormFields);

		// save/update decision and fill/update form fields
		String idNextNode = currentNode.getIdNextNode(requestData);
		Node nextNode = form.getNode(idNextNode);
		prepNextDecision(formData.decisionMap, decision, nextNode);
		saveDecision(formData.decisionMap, decision);
		if (currentNode.isOutputNode) {
			currentNode.fillFormFields(decision.rawInput, filledFormFields);
		}
		// update the FormData in the FormDataStore
		formDataStore.setFormData(owner, formData);

		/*
		 * Return decision associated with next node. If next node not visible,
		 * make decision for it and repeat.
		 */
		if (!nextNode.isVisible) {
			return makeDecision(owner, decision.next.context.id, requestData);
		}
		return decision.next;
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

	/*
	 * If there is no decision associated with the next node in the sequence,
	 * create one. Set it's "previous" field to point back to the context of the
	 * current node.
	 * 
	 * If the specified decision has a next node associated with it, this will
	 * create or update the decision associated with the next node. This allows
	 * the decision-tree to be traversed backwards.
	 */
	private static void prepNextDecision(DecisionMap decisions,
			Decision currDecision, Node nextNode) {
		Decision nextDecision = currDecision.next;
		if (nextDecision != null) {
			nextDecision.previous = currDecision;
		} else {
			nextDecision = new Decision()
				.setContext(nextNode)
				.setPrevious(currDecision);
			currDecision.next = nextDecision;
			decisions.putDecision(currDecision.next);
		}
	}

	private static void saveDecision(DecisionMap decisions, Decision decision) {
		Decision existingDecision = decisions.getDecision(decision.context.id);
		if (existingDecision != null) {
			// update existing decision
			existingDecision.next = decision.next;
			existingDecision.rawInput = decision.rawInput;
		} else {
			// save new decision
			decisions.putDecision(decision);
		}
	}
}
