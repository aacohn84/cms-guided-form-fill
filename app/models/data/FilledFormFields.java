package models.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class FilledFormFields implements
		Iterable<FilledFormFields.FilledFormField> {

	public static class FilledFormField {
		public String name;
		public String value;
	}

	private class FilledFormFieldIterator implements Iterator<FilledFormField> {
		Iterator<Entry<String, String>> underlyingIterator;

		public FilledFormFieldIterator() {
			underlyingIterator = filledFormFields.entrySet().iterator();
		}

		@Override
		public boolean hasNext() {
			return underlyingIterator.hasNext();
		}

		@Override
		public FilledFormField next() {
			Entry<String, String> nextEntry = underlyingIterator.next();
			FilledFormField nextField = new FilledFormField();
			nextField.name = nextEntry.getKey();
			nextField.value = nextEntry.getValue();
			return nextField;
		}

		@Override
		public void remove() {
			underlyingIterator.remove();
		}
	}

	private Map<String, String> filledFormFields;

	public FilledFormFields() {
		filledFormFields = new HashMap<String, String>();
	}

	public void copyTo(FilledFormFields intermediates) {
		for (Entry<String, String> entry : filledFormFields.entrySet()) {
			intermediates.fillField(entry.getKey(), entry.getValue());
		}
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

	@Override
	public Iterator<FilledFormField> iterator() {
		return new FilledFormFieldIterator();
	}
}
