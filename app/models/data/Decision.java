package models.data;

import models.tree.Node;

public class Decision {
	public Node context;
	public String rawInput;

	/**
	 * Construct a Decision with the specified context and rawInput.
	 * 
	 * @param context
	 *            - Node for which the rawInput applies.
	 * @param rawInput
	 *            - String denoting what the user's input was for the specified
	 *            node.
	 */
	public Decision(Node context, String rawInput) {
		this.context = context;
		this.rawInput = rawInput;
	}
}
