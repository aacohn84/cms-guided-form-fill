package core;

import java.util.Map;
import java.util.Queue;

import models.data.Decision;
import models.data.DecisionQueue;
import models.data.DecisionStore;
import models.data.InMemoryDecisionStore;
import models.forms.CMSForm;
import models.forms.ChangeOrderForm;
import models.tree.Node;

public class CMSGuidedFormFill {

	public static Queue<Decision> getFormOutput(String owner) {
		DecisionQueue decisionQueue = getDecisionQueue(
				InMemoryDecisionStore.getInstance(), owner);
		return decisionQueue.decisions;
	}
	
	/**
	 * Save a decision made by the owner of the form.
	 * 
	 * @param owner - name of the user who made the decision.
	 * @param decision
	 */
	static DecisionQueue getDecisionQueue(DecisionStore decisionStore,
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
	static void saveDecision(String owner, Decision decision) {
		// TODO: Cache the DecisionQueue
		DecisionStore decisionStore = InMemoryDecisionStore.getInstance();
		DecisionQueue decisionQueue = getDecisionQueue(decisionStore, owner);
		decisionQueue.addDecision(decision);
		decisionStore.putDecisionQueue(owner, decisionQueue);
	}

	/**
	 * Saves the decision and returns the next node in the form.
	 * @param idCurrentNode - id of the node 
	 * @param requestData
	 * @return
	 */
	public static Node getNextNode(String owner, String idCurrentNode,
			Map<String, String> requestData) {
		
		CMSForm form = ChangeOrderForm.getInstance();
		Node currentNode = form.getNode(idCurrentNode);

		// validate user input

		// save user input
		String serializedInput = currentNode.serializeInput(requestData);
		Decision decision = new Decision(currentNode, serializedInput);
		saveDecision(owner, decision);

		// retrieve next node
		String idNextNode = currentNode.getIdNextNode(requestData);
		return form.getNode(idNextNode);
	}
	
	public static Node getForm() {
		return ChangeOrderForm.getInstance().getRoot();
	}
}
