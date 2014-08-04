package models.tree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import play.twirl.api.Html;

/**
 * Abstraction of a node in a decision tree.
 * 
 * @author Aaron Cohn
 */
public abstract class Node {

	public String id = "";
	public String description = "";
	public boolean isOutputNode = false;

	public Node(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public Node(String id, String description, boolean isOutputNode) {
		this.id = id;
		this.description = description;
		this.isOutputNode = isOutputNode;
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

	public abstract Html renderSelectionAsHtml(String serializedSelection);

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
			throw new RuntimeException(e);
		}
		return serializedInput;
	}

	/**
	 * Returns the deserialized object.
	 * 
	 * @param serializedObj
	 *            - a String containing a serialized object.
	 */
	protected final Object recreateObject(String serializedObj) {
		Object obj = null;
		try {
			ByteArrayInputStream bytesIn = new ByteArrayInputStream(
					serializedObj.getBytes());
			ObjectInputStream objIn = new ObjectInputStream(bytesIn);
			obj = objIn.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return obj;
	}
}
