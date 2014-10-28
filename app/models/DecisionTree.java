package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

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

	// Variable names kept short in serialized classes to reduce JSON size.
	static class SerializedDecision {
		public String c; // context id
		public String p; // previous context id
		public String n; // next context id
		public String i; // serialized input
	}

	static class SerializedDecisionTree {
		public String f; // context id of first decision
		public String r; // context id of most recently made decision
		public SerializedDecision[] d; // serialized decisions
	}

	private Map<String, Decision> decisions;
	private Decision firstDecision;
	private Decision mostRecentlyMadeDecision;
	private CMSForm form;
	private boolean dirty;
	private boolean complete;

	public DecisionTree(CMSForm form) {
		this.decisions = new HashMap<String, Decision>();
		this.form = form;
		this.dirty = false;
		this.complete = false;
	}

	public Decision getDecision(String contextId) {
		return decisions.get(contextId);
	}

	public Decision getFirstDecision() {
		return firstDecision;
	}

	public Decision getMostRecentlyMadeDecision() {
		return mostRecentlyMadeDecision;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * A DecisionMap is complete if there is an unbroken path from the
	 * firstDecision to a Decision with a TerminalNode as its context.
	 */
	public boolean isComplete() {
		if (!dirty) {
			return complete;
		}
		Decision iter = firstDecision;
		while (iter.next != null) {
			iter = iter.next;
		}
		if (iter.context != null && iter.context.isTerminal()) {
			return (complete = true);
		}
		return (complete = false);
	}

	@Override
	public Iterator<Decision> iterator() {
		return new DecisionTreeIterator();
	}

	public Decision makeDecision(String contextId, Map<String, String> rawInput) {
		// retrieve the corresponding Decision if it exists, else create one
		boolean newDecision = false;
		Decision decision = getDecision(contextId);
		if (decision == null) {
			newDecision = true;
			decision = new Decision();
			decision.context = form.getNode(contextId);
			putDecision(decision);
		}

		// make decision if new input differs from old input
		Node context = decision.context;
		String newInput = context.serializeInput(rawInput);
		if (newDecision || inputChanged(decision, newInput)) {
			decision.serializedInput = newInput;
			dirty = true;

			// link to next decision if possible (if it doesn't exist, create it)
			// link with next decision if possible
			if (!context.isTerminal()) {
				String idNextNode = context.getIdNextNode(rawInput);
				Decision next = getDecision(idNextNode);
				if (next == null) {
					next = new Decision();
					next.context = form.getNode(idNextNode);
					linkDecisions(decision, next);
					putDecision(next);
				} else {
					linkDecisions(decision, next);
					if (context.isBranchingNode()) {
						updateActivePath(next);
					}
				}
			}
			if (context.isVisible) {
				mostRecentlyMadeDecision = decision;
			}
		}
		return decision;
	}

	private void linkDecisions(Decision d1, Decision d2) {
		d2.previous = d1;
		d1.next = d2;
	}

	/*
	 * Updates the active path to the right of the decision so that it is
	 * consistent both forward and backward.
	 */
	private void updateActivePath(Decision decision) {
		Decision curr = decision;
		while (curr.next != null) {
			curr.next.previous = curr;
			curr = curr.next;
		}
	}

	private boolean inputChanged(Decision decision, String newInput) {
		return !decision.serializedInput.equals(newInput);
	}

	public void putDecision(Decision decision) {
		if (decisions.isEmpty()) {
			firstDecision = mostRecentlyMadeDecision = decision;
		}
		decisions.put(decision.context.id, decision);
	}

	public String serialize() {
		ArrayList<SerializedDecision> list = new ArrayList<>();
		for (Decision d : decisions.values()) {
			SerializedDecision sd = new SerializedDecision();
			sd.c = d.context.id;
			sd.p = d.previous != null ? d.previous.context.id : null;
			sd.n = d.next != null ? d.next.context.id : null;
			sd.i = d.serializedInput;
			list.add(sd);
		}
		SerializedDecisionTree sdt = new SerializedDecisionTree();
		sdt.f = firstDecision.context.id;
		sdt.r = mostRecentlyMadeDecision.context.id;
		sdt.d = list.toArray(new SerializedDecision[list.size()]);
		return Json.toJson(sdt).toString();
	}

	public void deserialize(String serializedDecisions) {
		JsonNode jsonNode = Json.parse(serializedDecisions);
		SerializedDecisionTree sdt = Json.fromJson(jsonNode,
				SerializedDecisionTree.class);
		for (SerializedDecision sd : sdt.d) {
			Decision d = decisionToDeserialize(sd.c);
			d.previous = decisionToDeserialize(sd.p);
			d.next = decisionToDeserialize(sd.n);
			d.serializedInput = sd.i;
		}
		firstDecision = getDecision(sdt.f);
		mostRecentlyMadeDecision = getDecision(sdt.r);
		dirty = false;
	}

	/*
	 * Deserialization helper -- returns the decision that belongs to the given
	 * context. If it doesn't exist, it will be created and stored for future
	 * retrieval. The only member field guaranteed to be filled is the context.
	 */
	private Decision decisionToDeserialize(String contextId) {
		if (contextId == null) {
			return null;
		}
		Decision decision = getDecision(contextId);
		if (decision == null) {
			decision = new Decision();
			decision.context = form.getNode(contextId);
			decisions.put(contextId, decision);
		}
		return decision;
	}
}
