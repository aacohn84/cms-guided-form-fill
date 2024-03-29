package core.tree;

import java.util.Map;

import models.Decision;
import models.FilledFormFields;
import play.libs.Json;
import play.twirl.api.Html;

import com.fasterxml.jackson.databind.JsonNode;

import core.forms.CMSForm;

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
	public Decision createDecision(Map<String, String> requestData,
			@SuppressWarnings("unused") FilledFormFields filledFormFields) {
		String rawInput = serializeInput(requestData);

		Decision decision = new Decision()
			.setContext(this)
			.setRawInput(rawInput);

		return decision;
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
	 * Returns true if, and only if, the node terminates the form.
	 */
	public boolean isTerminal() {
		return false;
	}

	/**
	 * Returns <code>true</code> if the output of
	 * {@link Node#getIdNextNode(Map)} varies, <code>false</code> if it always
	 * returns the same ID.
	 */
	public abstract boolean isBranchingNode();

	/**
	 * Returns an HTML representation of the node for rendering in a template.
	 *
	 * @param rawInput
	 *            - the rawInput from the decision associated with this node.
	 */
	public abstract Html renderAsHtml(String rawInput);

	/**
	 * Converts the input to an object and serializes it as a String.
	 *
	 * @param input
	 *            - a mapping of input field names to input data.
	 * @return Serialized object in String form
	 */
	public abstract String serializeInput(Map<String, String> input);

	/**
	 * Returns the deserialized object.
	 *
	 * @param serializedObj
	 *            - a String containing a serialized object.
	 */
	protected final <A> A recreateObject(String serializedObj, Class<A> clazz) {
		JsonNode jsonNode = Json.parse(serializedObj);
		return Json.fromJson(jsonNode, clazz);
	}

	/**
	 * Returns the serialized object as a String.
	 */
	protected final String serializeAsString(Object object) {
		return Json.toJson(object).toString();
	}
}
