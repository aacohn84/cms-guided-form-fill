package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import models.data.Decision;
import models.data.DecisionQueue;
import models.data.DecisionStore;
import models.data.InMemoryDecisionStore;
import models.forms.CMSForm;
import models.forms.ChangeOrderForm;
import models.tree.Node;
import play.data.Form;
import play.mvc.Result;
import views.html.questionnaire.backdrop;

public class FormFill extends SecureController {

	public static class RequestParams {
		public static final String CURRENT_NODE = "currentNode";
	}

	/*
	 * Provides the root node of the requested form.
	 */
	public static Result getForm() {
		CMSForm form = ChangeOrderForm.getInstance();
		return ok(backdrop.render(form.getRoot()));
	}

	public static Result getFormOutput() {
		DecisionQueue decisionQueue = getDecisionQueue(
				InMemoryDecisionStore.getInstance(), getUsername());
		
		Map<String, String> formData = new HashMap<>();
		for (Decision decision : decisionQueue) {
			if (decision.context.isOutputNode) {
				decision.context.fillFormFields(decision.rawInput, formData);
			}
		}
		
		for (Entry<String, String> entry : formData.entrySet()) {
			System.out.println("Name: " + entry.getKey());
			System.out.println("Data: " + entry.getValue());
			System.out.println();
		}

		return ok(views.html.questionnaire.output.render(decisionQueue.decisions));
	}

	/*
	 * Provides the next node of the current form.
	 */
	public static Result getNextNode() {
		// get current node
		CMSForm form = ChangeOrderForm.getInstance();
		Map<String, String> requestData = Form.form().bindFromRequest().data();
		String idCurrentNode = requestData.get(RequestParams.CURRENT_NODE);
		Node currentNode = form.getNode(idCurrentNode);

		// validate user input

		// save user input
		String serializedInput = currentNode.serializeInput(requestData);
		Decision decision = new Decision(currentNode, serializedInput);
		saveDecision(decision);

		// CMSSession.print();

		// retrieve next node
		String idNextNode = currentNode.getIdNextNode(requestData);
		Node nextNode = form.getNode(idNextNode);

		return ok(backdrop.render(nextNode));
	}

	private static DecisionQueue getDecisionQueue(DecisionStore decisionStore,
			String username) {
		DecisionQueue decisionQueue;
		if (decisionStore.containsUsername(username)) {
			decisionQueue = decisionStore.getDecisionQueue(username);
		} else {
			decisionQueue = new DecisionQueue();
		}
		return decisionQueue;
	}

	/*
	 * Saves the specified decision in the DecisionQueue of the currently
	 * logged-in user.
	 */
	private static void saveDecision(Decision decision) {
		// TODO: Cache the DecisionQueue
		DecisionStore decisionStore = InMemoryDecisionStore.getInstance();
		String username = getUsername();
		DecisionQueue decisionQueue = getDecisionQueue(decisionStore, username);
		decisionQueue.addDecision(decision);
		decisionStore.putDecisionQueue(username, decisionQueue);
	}
}
