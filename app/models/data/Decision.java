package models.data;

import models.tree.Node;

/**
 * Represents a decision made by the user while traversing a decision tree.
 * <p>
 * It is comprised of an input, and a context in which that input was given. A
 * Decision also knows which node is associated with the previous decision, and
 * which node is associated with the next decision, so that a sequence of
 * decisions is formed.
 * </p>
 * 
 * @author Aaron Cohn
 */
public class Decision {
	public Node context;
	public Decision previous, next;
	public String rawInput;

	public Decision() {}

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
