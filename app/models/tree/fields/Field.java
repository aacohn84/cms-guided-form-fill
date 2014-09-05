package models.tree.fields;

import play.twirl.api.Html;

public class Field implements Cloneable {
	public String label;
	public String name;
	public String value;
	public FieldType fieldType;
	
	public Field() {
		label = "";
		name = "";
		value = "";
		fieldType = FieldType.TEXT;
	}
	
	public Field setLabel(String label) {
		this.label = label;
		return this;
	}
	public Field setName(String name) {
		this.name = name;
		return this;
	}
	public Field setValue(String value) {
		this.value = value;
		return this;
	}
	public Field setFieldType(FieldType fieldType) {
		this.fieldType = fieldType;
		return this;
	}
	
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.textField.render(this);
	}
	
	@Override
	public Field clone() {
		try {
			return (Field) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(); // Can't happen
		}
	}
}