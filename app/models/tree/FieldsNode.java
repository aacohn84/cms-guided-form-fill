package models.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import play.twirl.api.Html;

/**
 * A node with fields to fill in. The fields are meant to be rendered as form
 * inputs in HTML, with FieldType representing the kind of input data that each
 * field will accept.
 * 
 * @author Aaron Cohn
 */
public class FieldsNode extends SingleTargetNode {
	public static class Field {
		String description;
		String name;
		FieldType type;

		public Field(String description, String name, FieldType type) {
			this.description = description;
			this.name = name;
			this.type = type;
		}
	}

	public static enum FieldType {
		NUMBER, TEXT;
	}

	/**
	 * A data structure for storing the user's selection, intended to be
	 * serialized and stored as a String.
	 * 
	 * @author Aaron Cohn
	 */
	private static class StoredSelection implements Serializable {
		private static class StoredField implements Serializable {
			private static final long serialVersionUID = -4801158335261387855L;

			public String name;
			public String value;
		}

		private static final long serialVersionUID = 1081518772940436931L;

		StoredField[] fields;
	}

	List<Field> fields = new ArrayList<>();

	public FieldsNode(String id, String idNext) {
		super(id, idNext, "Please enter the following: ", true);
	}

	/**
	 * Add an input field to this FieldsNode.
	 * 
	 * @param fieldDescription
	 *            - the text to be displayed alongside the input field.
	 * @param name
	 *            - the name of the corresponding field in the Acrobat form.
	 * @param type
	 *            - the type of input expected by this field
	 * @return a reference to this FieldsNode so that calls to addField can be
	 *         chained together.
	 */
	public FieldsNode addField(String fieldDescription, String name,
			FieldType type) {
		Field field = new Field(fieldDescription, name, type);
		fields.add(field);
		return this;
	}

	@Override
	public void fillFormFields(String serializedObj,
			Map<String, String> formFields) {
		StoredSelection ss = (StoredSelection) recreateObject(serializedObj);
		for (StoredSelection.StoredField storedField : ss.fields) {
			formFields.put(storedField.name, storedField.value);
		}
	}

	@Override
	public String getNodeHtml() {
		String html = new String();
		for (Field field : fields) {
			String type = field.type.toString();
			html += field.description + ": <input type=\"" + type
					+ "\" name=\"" + field.description + "\"><br>";
		}
		return html;
	}

	@Override
	public Html renderSelectionAsHtml(String serializedSelection) {
		String html = new String();
		StoredSelection storedSelection = (StoredSelection) recreateObject(serializedSelection);
		for (StoredSelection.StoredField field : storedSelection.fields) {
			html += "<b>" + field.name + ":</b> " + field.value + "<br>";
		}
		return new Html(html);
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		// create a list of field names & values
		List<StoredSelection.StoredField> storedFields = new ArrayList<>();
		for (Field field : fields) {
			StoredSelection.StoredField storedField = new StoredSelection.StoredField();
			storedField.name = field.description;
			storedField.value = input.get(field.description);
			storedFields.add(storedField);
		}
		// convert list to plain array for storage
		StoredSelection storedSelection = new StoredSelection();
		storedSelection.fields = new StoredSelection.StoredField[storedFields
				.size()];
		storedSelection.fields = storedFields.toArray(storedSelection.fields);

		return serializeAsString(storedSelection);
	}
}
