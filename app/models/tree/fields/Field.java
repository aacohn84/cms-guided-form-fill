package models.tree.fields;

public class Field {
	public String description;
	public String name;
	public FieldType fieldType;

	public Field(String description, String name, FieldType fieldType) {
		this.description = description;
		this.name = name;
		this.fieldType = fieldType;
	}
}