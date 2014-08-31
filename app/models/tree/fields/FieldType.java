package models.tree.fields;

public enum FieldType {
	NUMBER("0.00"), TEXT("");

	private String defaultVal;
	
	private FieldType(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	
	public String defaultVal() {
		return this.defaultVal;
	}
}