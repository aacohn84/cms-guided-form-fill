package models.tree.fields;

public class FilledField extends Field {
	String value;
	
	public FilledField(String name, String value) {
		super(null, name, FieldType.TEXT);
		this.value = value;
	}
}