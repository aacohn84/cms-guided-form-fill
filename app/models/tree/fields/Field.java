package models.tree.fields;

public class Field {
	String description;
	String name;
	FieldType type;

	public Field(String description, String name, FieldType type) {
		this.description = description;
		this.name = name;
		this.type = type;
	}
}