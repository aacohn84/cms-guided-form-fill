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
import models.tree.CalculationNode;
import models.tree.Node;

public class CMSGuidedFormFill {

	public static Decision getFirstDecision(String owner) {
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		Node root = ChangeOrderForm.getInstance().getRoot();
		if (!formDataStore.containsUsername(owner)) {
			FormData formData = new FormData(owner);
			Decision firstDecision = new Decision(root, null, null);
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
		PDFFormFiller ff = new PDFFormFiller();
		return ff.fillForm(form, fields);
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
		 * Retrieve the owner's form data first. We won't bother doing anything
		 * else if this raises an exception.
		 */
		FormDataStore formDataStore = InMemoryFormDataStore.getInstance();
		FormData formData = formDataStore.getFormData(owner);

		// retrieve the current node
		CMSForm form = ChangeOrderForm.getInstance();
		Node currentNode = form.getNode(idCurrentNode);

		// TODO: validate user input

		// create a decision based on the data available
		FilledFormFields filledFormFields = formData.filledFormFields;
		Decision decision = currentNode.createDecision(form, requestData,
				filledFormFields);

		// save/update decision and fill/update form fields
		prepNextDecision(formData.decisionMap, decision);
		saveDecision(formData.decisionMap, decision);
		if (currentNode.isOutputNode) {
			currentNode.fillFormFields(decision.rawInput, filledFormFields);
		}
		// update the FormData in the FormDataStore
		formDataStore.setFormData(owner, formData);

		// return the decision associated with the next node
		String idNextNode = currentNode.getIdNextNode(requestData);

		/*
		 * If the next node is a CalculationNode, process the calculation but do
		 * not return its decision. Return the next one instead.
		 */
		Node nextNode = form.getNode(idNextNode);
		if (nextNode instanceof CalculationNode) {
			return makeDecision(owner, idNextNode, requestData);
		}
		return formData.decisionMap.getDecision(idNextNode);
	}

	static void saveDecision(DecisionMap decisions, Decision decision) {
		Decision existingDecision = decisions.getDecision(decision.context.id);
		Decision decisionToSave;
		if (existingDecision != null) {
			existingDecision.next = decision.next;
			existingDecision.rawInput = decision.rawInput;
			decisionToSave = existingDecision;
		} else {
			decisionToSave = decision;
		}
		decisions.putDecision(decisionToSave);
	}

	/*
	 * Set the previous and context fields for the decision associated with the
	 * next node in the sequence.
	 * 
	 * If the specified decision has a next node associated with it, this will
	 * create or update the decision associated with the next node. This allows
	 * the decision-tree to be traversed backwards.
	 */
	static void prepNextDecision(DecisionMap decisions, Decision decision) {
		if (decision.next != null) {
			Decision nextDecision = decisions.getDecision(decision.next.id);
			if (nextDecision != null) {
				nextDecision.previous = decision.context;
			} else {
				nextDecision = new Decision();
				nextDecision.previous = decision.context;
				nextDecision.context = decision.next;
			}
			decisions.putDecision(nextDecision);
		}
	}
}
