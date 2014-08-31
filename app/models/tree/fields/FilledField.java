package models.tree.fields;

public class FilledField extends Field {
	String value;
	
	public FilledField(String name, String value, FieldType type) {
		super(null, name, type);
		this.value = value;
	}
}