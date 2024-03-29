package core.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.FilledFormFields;

import org.apache.commons.lang3.StringUtils;

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
		 * Label to be shown alongside the radio button in the ChoiceNode's HTML
		 * representation.
		 */
		public String label;

		/**
		 * The name of the choice as used in the Adobe Acrobat form.
		 */
		public String name;

		/**
		 * ID of the node that comes after this one if this choice is selected.
		 */
		public String targetId;

		public Choice(String label, String targetId) {
			this.name = null;
			this.label = label;
			this.targetId = targetId;
		}

		public Choice(String label, String choiceName, String targetId) {
			this.label = label;
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
	public static class StoredSelection implements Serializable {
		private static final long serialVersionUID = 3979783495632227041L;

		public String choice;
	}

	/**
	 * A mapping of each choice to the identifier of its target node.
	 */
	Map<String, Choice> choicesToTargetIds = new HashMap<>();

	/**
	 * A list of the choices in the order they were added to the node.
	 */
	List<Choice> orderedChoices = new ArrayList<>();

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
	 * @param label
	 *            - text describing the choice.
	 * @param targetId
	 *            - id of the node corresponding to the option.
	 */
	public ChoiceNode addChoice(String label, String targetId) {
		Choice choice = new Choice(label, targetId);
		choicesToTargetIds.put(label, choice);
		orderedChoices.add(choice);
		return this;
	}

	/**
	 * Adds an option that points to the target node.
	 *
	 * @param label
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
	public ChoiceNode addChoice(String label, String choiceName,
			String targetId) {
		Choice choice = new Choice(label, choiceName, targetId);
		choicesToTargetIds.put(label, choice);
		orderedChoices.add(choice);
		return this;
	}

	@Override
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {
		StoredSelection ss = recreateObject(serializedObj,
				StoredSelection.class);
		Choice choice = choicesToTargetIds.get(ss.choice);
		formFields.fillField(fieldName, choice.name);
	}

	@Override
	public String getIdNextNode(Map<String, String> input) {
		if (input.containsKey("choice")) {
			String choice = input.get("choice");
			return choicesToTargetIds.get(choice).targetId;
		}
		throw new RuntimeException(
				"Input for a ChoiceNode did not contain a \"choice\" field.");
	}

	@Override
	public Html renderAsHtml(String rawInput) {
		String choice = "";
		if (StringUtils.isNotEmpty(rawInput)) {
			StoredSelection ss = recreateObject(rawInput, StoredSelection.class);
			choice = ss.choice;
		}
		return views.html.questionnaire.choice.render(choice, orderedChoices);
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		String choice = input.get("choice");
		StoredSelection storedSelection = new StoredSelection();
		storedSelection.choice = choice;
		return serializeAsString(storedSelection);
	}

	@Override
	public boolean isBranchingNode() {
		return true;
	}
}
