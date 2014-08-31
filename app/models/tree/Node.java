package models.tree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import models.data.Decision;
import models.data.FilledFormFields;
import models.forms.CMSForm;
import play.twirl.api.Html;

/**
 * Abstraction of a node in a decision tree.
 * 
 * @author Aaron Cohn
 */
public abstract class Node {
	public String description = "";
	public String id = "";
	public boolean isOutputNode = false;
	public boolean isVisible = true;

	public Node(String id, String description) {
		this.id = id;
		this.description = description;
	}

	public Node(String id, String description, boolean isOutputNode) {
		this.id = id;
		this.description = description;
		this.isOutputNode = isOutputNode;
	}
	
	public Node(String id, String description, boolean isOutputNode,
			boolean isVisible) {
		this.id = id;
		this.description = description;
		this.isOutputNode = isOutputNode;
		this.isVisible = isVisible;
	}

	/**
	 * Creates a {@link Decision} from the available data.
	 * 
	 * @param form
	 *            - the {@link CMSForm} instance associated with this decision.
	 * @param requestData
	 *            - the keys and values of input retrieved from the user.
	 * @param filledFormFields
	 *            - form fields that have been filled previously. These values
	 *            are typically used by calculations that refer to other form
	 *            fields.
	 * @return a {@link Decision} prepared with all fields except
	 *         {@link Decision#previous}
	 */
	public Decision createDecision(CMSForm form,
			Map<String, String> requestData,
			@SuppressWarnings("unused") FilledFormFields filledFormFields) {
		String rawInput = serializeInput(requestData);
		String idNextNode = getIdNextNode(requestData);
		Node nextNode = form.getNode(idNextNode);

		return new Decision(this, rawInput, nextNode);
	}

	/**
	 * Uses the given input to fill any form fields associated with this node.
	 * The filled fields are returned in a map where the key is the name of the
	 * field, and the value is the input used to fill it.
	 * 
	 * @param serializedObj
	 *            - a String containing a serialized object of saved user input.
	 * 
	 * @param formFields
	 *            - a map that is either empty or contains filled form fields
	 *            for the same form.
	 */
	public abstract void fillFormFields(String serializedObj,
			FilledFormFields formFields);

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
	 * 
	 * @param rawInput
	 *            - the rawInput from the decision associated with this node.
	 */
	public final Html renderAsHtml(String rawInput) {
		return new Html(getNodeHtml(rawInput));
	}

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
	 * Returns a String containing the HTML representation of the node.
	 * 
	 * @param rawInput
	 *            - the rawInput from the decision associated with this node.
	 */
	protected abstract String getNodeHtml(String rawInput);

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
}
