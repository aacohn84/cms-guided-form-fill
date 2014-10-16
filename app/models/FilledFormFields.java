package models;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FilledFormFields implements
		Iterable<FilledFormFields.FilledFormField> {

	public static class FilledFormField {
		public String name;
		public String value;

		public FilledFormField(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	private Map<String, FilledFormField> filledFormFields;

	public FilledFormFields() {
		filledFormFields = new HashMap<String, FilledFormField>();
	}

	public void copyTo(FilledFormFields other) {
		for (FilledFormField field : filledFormFields.values()) {
			other.fillField(field.name, field.value);
		}
	}

	public void fillField(String name, String value) {
		FilledFormField field = filledFormFields.get(name);
		if (field != null) {
			field.value = value;
		} else {
			filledFormFields.put(name, new FilledFormField(name, value));
		}
	}

	public String getFieldValue(String name) {
		FilledFormField field = filledFormFields.get(name);
		if (field == null) {
			throw new RuntimeException(
					"The specified field has not been filled: " + name);
		}
		return field.value;
	}

	public boolean isFieldFilled(String name) {
		return filledFormFields.containsKey(name);
	}

	@Override
	public Iterator<FilledFormField> iterator() {
		return filledFormFields.values().iterator();
	}
}
