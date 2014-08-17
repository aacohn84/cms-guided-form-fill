package models.data;

import java.util.HashMap;
import java.util.Map;

import models.tree.Node;

/**
 * The main data structure involved in forming a decision tree.
 * <p>
 * It allows access to each {@link Decision} using the identifier of the
 * {@link Node} associated with it. Each decision tracks the nodes associated
 * with the previous and next decisions, which allows a sequence of decisions to
 * form. By taking multiple paths through the form, we get multiple sequences,
 * which form a partially connected tree.
 * </p>
 * 
 * @author Aaron Cohn
 */
public class DecisionMap {
	Map<String, Decision> decisions;

	public DecisionMap() {
		decisions = new HashMap<String, Decision>();
	}

	public Decision getDecision(String contextId) {
		return decisions.get(contextId);
	}

	public void putDecision(Decision decision) {
		decisions.put(decision.context.id, decision);
	}
}
