package models.tree.fields;

public enum FieldType {
	EMAIL(""),
	HIDDEN(""),
	NUMBER("0.00"),
	TEL(""),
	TEXT(""),
	TEXTAREA("");

	private String defaultVal;
	
	private FieldType(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	
	public String defaultVal() {
		return this.defaultVal;
	}
}