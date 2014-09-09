package models.tree.fields;

import play.twirl.api.Html;

public class Field implements Cloneable {
	public static class HiddenField extends Field {
		@Override
		public Html renderAsHtml() {
			return views.html.questionnaire.fieldTypes.hiddenField.render(this);
		}
	}

	public static class NumberField extends Field {
		@Override
		public Html renderAsHtml() {
			return views.html.questionnaire.fieldTypes.numberField.render(this);
		}
	}

	public static Field newField(FieldType fieldType) {
		return newField("", "", fieldType);
	}

	public static Field newField(String label, String name, FieldType fieldType) {
		Field field;
		switch (fieldType) {
			case NUMBER:	field = new NumberField(); break;
			case HIDDEN:	field = new HiddenField(); break;
			default:		field = new Field(); // text-variant
		}
		field.setLabel(label);
		field.setName(name);
		field.setFieldType(fieldType);
		return field;
	}

	public FieldType fieldType;
	public boolean isRequired;
	public String label;
	public String name;
	public String value;

	public Field() {
		fieldType = FieldType.TEXT;
		isRequired = true;
		label = "";
		name = "";
		value = "";
	}

	@Override
	public final Field clone() {
		try {
			return (Field) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(); // Can't happen
		}
	}

	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.textField.render(this);
	}

	public final Field setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
		return this;
	}

	public final Field setLabel(String label) {
		this.label = label;
		return this;
	}

	public final Field setName(String name) {
		this.name = name;
		return this;
	}
	
	public final Field setRequired(boolean isRequired) {
		this.isRequired = isRequired;
		return this;
	}

	public final Field setValue(String value) {
		this.value = value;
		return this;
	}
}
