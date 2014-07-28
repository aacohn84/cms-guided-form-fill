package models.tree;

import java.util.Map;

import play.twirl.api.Html;

/**
 * Abstraction of a node in a decision tree.
 * 
 * @author Aaron Cohn
 * 
 */
public abstract class Node {

	public String id;

	public Node(String id) {
		this.id = id;
	}

	/**
	 * Returns the identifier of the next node in the tree based on the input
	 * given.
	 * 
	 * @param input
	 *            - a mapping of input field names to input data.
	 */
	public abstract String idNextNode(Map<String, String> input);
	
	public abstract Html renderAsHtml();
}