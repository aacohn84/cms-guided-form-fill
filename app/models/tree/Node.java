package models.tree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import play.twirl.api.Html;

/**
 * Abstraction of a node in a decision tree.
 * 
 * @author Aaron Cohn
 */
public abstract class Node {

	public String id;
	public String description;

	public Node(String id, String description) {
		this.id = id;
		this.description = description;
	}

	/**
	 * Returns the identifier of the next node in the tree based on the input
	 * given.
	 * 
	 * @param input
	 *            - a mapping of input field names to input data.
	 */
	public abstract String getIdNextNode(Map<String, String> input);

	/**
	 * Returns an HTML representation of the node for rendering in a template.
	 */
	public final Html renderAsHtml() {
		return new Html(getNodeHtml());
	}

	/**
	 * Returns a String containing the HTML representation of the node.
	 */
	protected abstract String getNodeHtml();

	/**
	 * Converts the input to an object and serializes it as a String.
	 * 
	 * @param input
	 *            - a mapping of input field names to input data.
	 * @return Serialized object in String form
	 */
	public abstract String serializeInput(Map<String, String> input);

	/**
	 * Returns the serialized object as a String.
	 */
	protected final String serializeAsString(Object object) {
		String serializedInput = "";
		try {
			ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(bytesOut);
			objOut.writeObject(object);
			serializedInput = bytesOut.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return serializedInput;
	}
}
