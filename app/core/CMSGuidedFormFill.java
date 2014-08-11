package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.data.Decision;
import models.data.DecisionMap;
import models.data.DecisionStore;
import models.data.InMemoryDecisionStore;
import models.data.NoSuchUserException;
import models.forms.CMSForm;
import models.forms.ChangeOrderForm;
import models.tree.Node;

public class CMSGuidedFormFill {

	public static Node getForm(String owner) {
		DecisionStore decisionStore = InMemoryDecisionStore.getInstance();
		if (!decisionStore.containsUsername(owner)) {
			decisionStore.putDecisions(owner, new DecisionMap());
		}
		return ChangeOrderForm.getInstance().getRoot();
	}

	/**
	 * Returns a list of decisions corresponding to the owner's path through the
	 * decision tree. Only decisions that result in form output are listed.
	 */
	public static List<Decision> getFormOutput(String owner) {
		DecisionMap decisions = getDecisions(
				InMemoryDecisionStore.getInstance(), owner);

		List<Decision> formOutput = new ArrayList<>();
		Node currNode = ChangeOrderForm.getInstance().getRoot();
		Decision currDecision = decisions.getDecision(currNode.id);
		while (currDecision != null) {
			if (currNode.isOutputNode) {
				formOutput.add(currDecision);
			}
			if (currDecision.next == null) {
				break;
			}			
			currNode = currDecision.next;
			currDecision = decisions.getDecision(currNode.id);
			
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
	public static Node getNextNode(String owner, String idCurrentNode,
			Map<String, String> requestData) {

		CMSForm form = ChangeOrderForm.getInstance();
		Node currentNode = form.getNode(idCurrentNode);

		// validate user input

		// retrieve next node
		String idNextNode = currentNode.getIdNextNode(requestData);
		Node nextNode = form.getNode(idNextNode);

		// save decision
		String serializedInput = currentNode.serializeInput(requestData);

		Decision decision = new Decision(currentNode, serializedInput, nextNode);
		DecisionStore decisionStore = InMemoryDecisionStore.getInstance();
		DecisionMap decisions = decisionStore.getDecisions(owner);
		prepNextDecision(decisions, decision);
		saveDecision(decisions, decision);

		return nextNode;
	}

	/**
	 * Save a decision made by the owner of the form.
	 * 
	 * @param owner
	 *            - name of the user who made the decision.
	 * @param decision
	 */
	static DecisionMap getDecisions(DecisionStore decisionStore, String username) {
		DecisionMap decisions;
		if (decisionStore.containsUsername(username)) {
			decisions = decisionStore.getDecisions(username);
		} else {
			decisions = new DecisionMap();
		}
		return decisions;
	}

	static void saveDecision(DecisionMap decisions, Decision decision) {
		// TODO: Cache the DecisionQueue
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
	 * If the specified decision has a next node associated with it, this will
	 * create or update the decision associated with the next node so that it
	 * points back to this decision's associated node as the previous one in the
	 * sequence. This is the main way that decisions know which node to
	 * associate with the previous decision.
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
