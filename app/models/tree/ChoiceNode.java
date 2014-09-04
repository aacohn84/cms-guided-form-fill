package models.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import models.data.FilledFormFields;
import play.twirl.api.Html;

/**
 * A node with some mutually exclusive options to choose from, intended to be
 * implemented with radio-buttons in HTML.
 * 
 * @author Aaron Cohn
 */
public class ChoiceNode extends Node {

	/**
	 * A data structure representing a choice in a ChoiceNode.
	 * 
	 * @author Aaron Cohn
	 */
	public static class Choice {
		/**
		 * Description to be shown alongside the radio button in the
		 * ChoiceNode's HTML representation.
		 */
		public String description;

		/**
		 * The name of the choice as used in the Adobe Acrobat form.
		 */
		public String name;

		/**
		 * ID of the node that comes after this one if this choice is selected.
		 */
		public String targetId;

		public Choice(String description, String targetId) {
			this.name = null;
			this.description = description;
			this.targetId = targetId;
		}

		public Choice(String description, String choiceName, String targetId) {
			this.description = description;
			this.name = choiceName;
			this.targetId = targetId;
		}
	}

	/**
	 * A data structure for storing the user's selection, intended to be
	 * serialized and stored as a String.
	 * 
	 * @author Aaron Cohn
	 */
	private static class StoredSelection implements Serializable {
		private static final long serialVersionUID = 3979783495632227041L;

		public String choice;

		public StoredSelection(String choice) {
			this.choice = choice;
		}
	}

	/**
	 * A mapping of each option to the identifier of its target node.
	 */
	Map<String, Choice> choices = new HashMap<>();

	/**
	 * The name of the form-field associated with this choice, as used in the
	 * Adobe Acrobat form.
	 */
	String fieldName;

	/**
	 * Construct a ChoiceNode that will send the user to another node but
	 * doesn't fill a form-field.
	 * 
	 * @param id
	 *            - a unique identifier for this node.
	 * @param description
	 *            - a description of the choice to be made by the user.
	 */
	public ChoiceNode(String id, String description) {
		super(id, description);
	}

	/**
	 * Constructs a ChoiceNode associated with a form-field.
	 * 
	 * @param id
	 *            - a unique identifier for this node.
	 * @param description
	 *            - a description of the choice to be made by the user.
	 * @param fieldName
	 *            - the name of the field associated with this node, as used in
	 *            the Adobe Acrobat form.
	 */
	public ChoiceNode(String id, String description, String fieldName) {
		super(id, description, true);
		this.fieldName = fieldName;
	}

	/**
	 * Adds a choice that points to the target node.
	 * 
	 * @param choiceDescription
	 *            - text describing the choice.
	 * @param targetId
	 *            - id of the node corresponding to the option.
	 */
	public ChoiceNode addChoice(String choiceDescription, String targetId) {
		Choice choice = new Choice(choiceDescription, targetId);
		choices.put(choiceDescription, choice);
		return this;
	}

	/**
	 * Adds an option that points to the target node.
	 * 
	 * @param choiceDescription
	 *            - text describing this choice.
	 * @param choiceName
	 *            - name of the form field associated with this choice.
	 * @param targetId
	 *            - ID of the node provided by
	 *            {@link ChoiceNode#getIdNextNode(Map)} if this choice is
	 *            selected.
	 * @return a reference to this ChoiceNode so that calls to addChoice can be
	 *         chained together.
	 */
	public ChoiceNode addChoice(String choiceDescription, String choiceName,
			String targetId) {
		Choice choice = new Choice(choiceDescription, choiceName, targetId);
		choices.put(choiceDescription, choice);
		return this;
	}

	@Override
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {
		StoredSelection ss = (StoredSelection) recreateObject(serializedObj);
		Choice choice = choices.get(ss.choice);
		formFields.fillField(fieldName, choice.name);
	}

	@Override
	public String getIdNextNode(Map<String, String> input) {
		if (input.containsKey("choice")) {
			String choice = input.get("choice");
			return choices.get(choice).targetId;
		}
		throw new RuntimeException(
				"Input for a ChoiceNode did not contain a \"choice\" field.");
	}

	@Override
	public Html renderAsHtml(String rawInput) {
		return views.html.questionnaire.choice.render(choices);
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		String choice = input.get("choice");
		StoredSelection storedSelection = new StoredSelection(choice);
		return serializeAsString(storedSelection);
	}
}
