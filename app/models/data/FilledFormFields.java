package models.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FilledFormFields {
	public Map<String, String> filledFormFields;

	public FilledFormFields() {
		filledFormFields = new HashMap<String, String>();
	}

	public void fillField(String name, String value) {
		filledFormFields.put(name, value);
	}

	public String getFieldValue(String name) {
		String fieldValue = filledFormFields.get(name);
		if (fieldValue == null) {
			throw new RuntimeException(
					"The specified field has not been filled: " + name);
		}
		return fieldValue;
	}

	public boolean isFieldFilled(String name) {
		return filledFormFields.containsKey(name);
	}

	public void copyTo(FilledFormFields intermediates) {
		for (Entry<String, String> entry : filledFormFields.entrySet()) {
			intermediates.fillField(entry.getKey(), entry.getValue());
		}
	}
}
