package models.data;

import models.tree.Node;

/**
 * Represents a decision made by the user while filling out a form.
 * <p>
 * Each decision is composed of a context and an input. {@link Decision#context}
 * is the node that the input applies to. {@link Decision#rawInput} is a string
 * which usually contains a serialized object created by the context node.
 * </p>
 * <p>
 * Each decision is part of a sequence. If this decision is first in the
 * sequence, then {@link Decision#previous} will be <code>null</code>. If this
 * decision is last in the sequence, then {@link Decision#next} will be
 * <code>null</code>.
 * </p>
 * 
 * @author Aaron Cohn
 */
public class Decision {
	public Node context;
	public Decision previous, next;
	public String rawInput;

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
		this.rawInput = rawInput;
		return this;
	}
}
