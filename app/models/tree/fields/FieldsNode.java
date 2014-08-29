package models.tree.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.data.FilledFormFields;
import models.tree.SingleTargetNode;
import play.twirl.api.Html;

/**
 * A node with fields to fill in. The fields are meant to be rendered as form
 * inputs in HTML, with FieldType representing the kind of input data that each
 * field will accept.
 * 
 * @author Aaron Cohn
 */
public class FieldsNode extends SingleTargetNode {
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
	
	public FieldsNode addFilledField(String name, String value) {
		Field field = new FilledField(name, value);
		fields.add(field);
		return this;
	}

	@Override
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {
		StoredSelection ss = (StoredSelection) recreateObject(serializedObj);
		for (StoredSelection.Field storedField : ss.fields) {
			formFields.fillField(storedField.name, storedField.value);
		}
	}

	@Override
	public String getNodeHtml(String rawInput) {
		String html = new String();
		for (Field field : fields) {
			if (!(field instanceof FilledField)) {
				String type = field.type.toString();
				html += field.description + ": <input type=\"" + type
						+ "\" name=\"" + field.description + "\"><br>";
			}
		}
		return html;
	}

	@Override
	public Html renderSelectionAsHtml(String serializedSelection) {
		String html = new String();
		StoredSelection storedSelection = (StoredSelection) recreateObject(serializedSelection);
		for (StoredSelection.Field field : storedSelection.fields) {
			html += "<b>" + field.name + ":</b> " + field.value + "<br>";
		}
		return new Html(html);
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		// create a list of field names & values
		List<StoredSelection.Field> storedFields = new ArrayList<>();
		for (Field field : fields) {
			StoredSelection.Field storedField = new StoredSelection.Field();
			storedField.name = field.name;
			if (field instanceof FilledField) {
				storedField.value = ((FilledField) field).value;
			} else {
				storedField.value = input.get(field.description);
			}
			storedFields.add(storedField);
		}
		// convert list to plain array for storage
		StoredSelection storedSelection = new StoredSelection();
		storedSelection.fields = new StoredSelection.Field[storedFields.size()];
		storedSelection.fields = storedFields.toArray(storedSelection.fields);

		return serializeAsString(storedSelection);
	}
}
