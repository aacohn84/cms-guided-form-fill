package models.tree.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
	private static Field newField(String label, String name, FieldType fieldType) {
		Field field;
		switch (fieldType) {
			case NUMBER: field = new NumberField(); break;
			case HIDDEN: field = new HiddenField(); break;
			default: 	 field = new Field(); // text field
		}
		field.setLabel(label);
		field.setName(name);
		field.setFieldType(fieldType);
		return field;
	}

	List<Field> fields = new ArrayList<>();

	public FieldsNode(String id, String idNext) {
		super(id, idNext, "Please enter the following: ", true);
	}

	/**
	 * Add an input field to this FieldsNode.
	 * 
	 * @param label
	 *            - the text to be displayed alongside the input field.
	 * @param name
	 *            - the name of the corresponding field in the Acrobat form.
	 * @param fieldType
	 *            - the type of input expected by this field
	 * @return a reference to this FieldsNode so that calls to addField can be
	 *         chained together.
	 */
	public FieldsNode addField(String label, String name, FieldType fieldType) {
		Field field = newField(label, name, fieldType);
		fields.add(field);
		return this;
	}

	public FieldsNode addFilledField(String name, String value,
			FieldType fieldType) {
		Field field = newField(null, name, fieldType);
		field.setValue(value);
		fields.add(field);
		return this;
	}

	@Override
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {
		StoredSelection ss = recreateObject(serializedObj, StoredSelection.class);
		for (StoredField storedField : ss.fields) {
			formFields.fillField(storedField.name, storedField.value);
		}
	}

	@Override
	public Html renderAsHtml(String rawInput) {
		if (StringUtils.isNotEmpty(rawInput)) {
			StoredSelection ss = recreateObject(rawInput, StoredSelection.class);
			assert(ss.fields.length == fields.size());
			int numFields = ss.fields.length;
			ArrayList<Field> displayedFields = new ArrayList<>(numFields);
			for (int i = 0; i < numFields; i++) {
				StoredField storedField = ss.fields[i];
				Field displayedField = fields.get(i).clone();
				displayedField.value = storedField.value;
				displayedFields.add(displayedField);
			}
			return views.html.questionnaire.fields.render(displayedFields);
		}
		return views.html.questionnaire.fields.render(fields);
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		// create a list of field names & values
		List<StoredField> storedFields = new ArrayList<>();
		for (Field field : fields) {
			StoredField storedField = new StoredField();
			storedField.name = field.name;
			boolean isPrefilledField = StringUtils.isNotEmpty(field.value);
			if (isPrefilledField) {
				storedField.value = field.value;
			} else {
				storedField.value = input.get(field.name);
			}
			storedFields.add(storedField);
		}
		// convert list to plain array for storage
		StoredSelection storedSelection = new StoredSelection();
		storedSelection.fields = new StoredField[storedFields.size()];
		storedSelection.fields = storedFields.toArray(storedSelection.fields);

		return serializeAsString(storedSelection);
	}
}
