package models.data;

import java.util.HashMap;
import java.util.Iterator;
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
public class DecisionMap implements Iterable<Decision> {

	/**
	 * Traverses the active path through the DecisionMap.
	 * 
	 * @author Aaron Cohn
	 */
	public class DecisionMapIterator implements Iterator<Decision> {
		Decision current;
		Decision negativeOne; // empty decision, serves to start iteration

		public DecisionMapIterator() {
			negativeOne = new Decision().setNext(firstDecision);
			current = negativeOne;
		}

		@Override
		public boolean hasNext() {
			return (current.next != null);
		}

		@Override
		public Decision next() {
			return (current = decisions.get(current.next.context.id));
		}

		@Override
		public void remove() {
			// no implementation
		}
	}

	Map<String, Decision> decisions;
	Decision firstDecision;

	public DecisionMap() {
		decisions = new HashMap<String, Decision>();
	}

	public Decision getDecision(String contextId) {
		return decisions.get(contextId);
	}

	public Decision getFirstDecision() {
		return firstDecision;
	}

	public void putDecision(Decision decision) {
		if (decisions.isEmpty()) {
			firstDecision = decision;
		}
		decisions.put(decision.context.id, decision);
	}

	@Override
	public Iterator<Decision> iterator() {
		return new DecisionMapIterator();
	}
}
