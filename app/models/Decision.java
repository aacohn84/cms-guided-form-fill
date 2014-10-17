package models;

import core.tree.Node;

/**
 * Represents a decision made by the user while filling out a form.
 * <p>
 * Each Decision corresponds to a single Node visited by the user. This Node is
 * called the context of the Decision. The Decision also stores the relevant
 * input given at that Node. The input is parsed and serialized by the context
 * Node, and stored here in String form as <code>Decision.serializedInput</code>.
 * </p>
 * <p>
 * Each decision is part of a sequence. If this is the first decision in the
 * sequence, then <code>Decision.previous == null</code>. If it's the last, then
 * <code>Decision.next == null</code>.
 * </p>
 * 
 * @author Aaron Cohn
 */
public class Decision {
	public Node context;
	public Decision previous, next;
	public String serializedInput;

	public Decision setContext(Node context) {
		this.context = context;
		return this;
	}

	public Decision setPrevious(Decision previous) {
		this.previous = previous;
		return this;
	}

	public Decision setNext(Decision next) {
		this.next = next;
		return this;
	}

	public Decision setRawInput(String rawInput) {
		this.serializedInput = rawInput;
		return this;
	}
}
