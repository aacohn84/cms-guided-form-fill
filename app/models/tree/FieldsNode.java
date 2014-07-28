package models.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A node with fields to fill in. Each field has a description and a type. The
 * fields are meant to be rendered as inputs in HTML.
 * 
 * @author Aaron Cohn
 */
public class FieldsNode extends SingleTargetNode {
	public static class Field {
		String description;
		FieldType type;

		public Field(String description, FieldType type) {
			this.description = description;
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
		super(id, idNext, "Please enter the following: ");
	}

	public FieldsNode addField(String fieldDescription, FieldType type) {
		Field field = new Field(fieldDescription, type);
		fields.add(field);
		return this;
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
		storedSelection.fields = new StoredSelection.StoredField[storedFields.size()];
		storedSelection.fields = storedFields.toArray(storedSelection.fields);

		return serializeAsString(storedSelection);
	}
}
