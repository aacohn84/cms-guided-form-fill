package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import play.libs.Json;
import core.forms.CMSForm;
import core.tree.Node;

/**
 * The main data structure involved in forming a decision tree.
 * <p>
 * It allows access to each {@link Decision} using the id of the Decision's
 * context {@link Node} as a key. Each Decision links to the previous and next
 * Decisions, which allows a sequence of decisions to form. By taking multiple
 * paths through the form, we get multiple sequences, which form a partially
 * connected tree.
 * </p>
 * <p>
 * Essentially, it's several doubly linked lists, each one reprsenting a section
 * of a path through the tree. Different sections can be linked together to form
 * a complete path through the tree. This is referred to as the active path, and
 * there can only be one in the tree at any given time.
 * </p>
 * 
 * @author Aaron Cohn
 */
public class DecisionTree implements Iterable<Decision> {
	/**
	 * Traverses the active path through the DecisionTree.
	 * 
	 * @author Aaron Cohn
	 */
	public class DecisionTreeIterator implements Iterator<Decision> {
		Decision current;
		Decision negativeOne; // empty decision, serves to start iteration

		public DecisionTreeIterator() {
			negativeOne = new Decision().setNext(firstDecision);
			current = negativeOne;
		}

		@Override
		public boolean hasNext() {
			return (current.next != null);
		}

		@Override
		public Decision next() {
			return (current = current.next);
		}

		@Override
		public void remove() {
			// no implementation
		}
	}

	static class SerializedDecision implements Serializable {
		private static final long serialVersionUID = -7893808643530736707L;

		public String contextId;
		public String serializedInput;
	}

	Map<String, Decision> decisions;
	Decision firstDecision;
	CMSForm form;

	public DecisionTree(CMSForm form) {
		decisions = new HashMap<String, Decision>();
		this.form = form;
	}

	public Decision getDecision(String contextId) {
		return decisions.get(contextId);
	}

	public Decision getFirstDecision() {
		return firstDecision;
	}

	/**
	 * A DecisionMap is complete if there is an unbroken path from the
	 * firstDecision to a Decision with a TerminalNode as its context.
	 */
	public boolean isComplete() {
		Decision iter = firstDecision;
		while (iter.next != null) {
			iter = iter.next;
		}
		if (iter.context != null && iter.context.isTerminal()) {
			return true;
		}
		return false;
	}

	@Override
	public Iterator<Decision> iterator() {
		return new DecisionTreeIterator();
	}

	public Decision makeDecision(String contextId, Map<String, String> rawInput) {
		// retrieve the corresponding Decision if it exists, else create one
		Decision decision = getDecision(contextId);
		if (decision == null) {
			decision = new Decision();
			decision.context = form.getNode(contextId);
			putDecision(decision);
		}

		// set the new input
		Node context = decision.context;
		decision.serializedInput = context.serializeInput(rawInput);

		// link to next decision if possible (if it doesn't exist, create it)
		if (!context.isTerminal()) {
			String idNextNode = context.getIdNextNode(rawInput);
			Decision next = getDecision(idNextNode);
			if (next == null) {
				next = new Decision();
				next.context = form.getNode(idNextNode);
				next.previous = decision;
				putDecision(next);
			}
			decision.next = next;
		}
		return decision;
	}

	public void putDecision(Decision decision) {
		if (decisions.isEmpty()) {
			firstDecision = decision;
		}
		decisions.put(decision.context.id, decision);
	}

	public String serialize() {
		ArrayList<SerializedDecision> list = new ArrayList<>();
		for (Decision decision : decisions.values()) {
			SerializedDecision sd = new SerializedDecision();
			sd.contextId = decision.context.id;
			sd.serializedInput = decision.serializedInput;
			list.add(sd);
		}
		SerializedDecision[] array = new SerializedDecision[list.size()];
		array = list.toArray(array);
		return Json.toJson(array).toString();
	}
}
