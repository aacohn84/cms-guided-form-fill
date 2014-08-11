package models.data;

import models.tree.Node;

/**
 * A decision made by the user while traversing a decision tree. A decision has
 * a context in which it was made, and it is part of a sequence.
 * 
 * @author Aaron Cohn
 * 
 */
public class Decision {
	public Node previous, next, context;
	public String rawInput;

	public Decision() {
	}

	/**
	 * Construct a Decision with the specified previous, next, context and
	 * rawInput.
	 * 
	 * @param previous
	 *            - Node preceding the one for which this decision applies.
	 * @param next
	 *            - Next node in the sequence as determined from the context and
	 *            input.
	 * @param context
	 *            - Node for which the rawInput applies.
	 * @param rawInput
	 *            - String denoting what the user's input was for the specified
	 *            context.
	 */
	public Decision(Node previous, Node next, Node context, String rawInput) {
		this.previous = previous;
		this.next = next;
		this.context = context;
		this.rawInput = rawInput;
	}

	/**
	 * Construct a Decision with the specified context, rawInput, and next node.
	 * 
	 * @param context
	 *            - Node for which the rawInput applies.
	 * @param next
	 *            - Next node in the sequence as determined from the context and
	 *            input.
	 * @param rawInput
	 *            - String denoting what the user's input was for the specified
	 *            node.
	 */
	public Decision(Node context, String rawInput, Node next) {
		this.context = context;
		this.rawInput = rawInput;
		this.next = next;
	}
}
