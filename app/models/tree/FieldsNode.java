package models.tree;

import java.util.List;

public class FieldsNode extends SingleTargetNode {
	public static class Field {
		String description;
		FieldType type;

		public Field(String description, FieldType type) {
			this.description = description;
			this.type = type;
		}
	}

	public static enum FieldType {
		DOLLAR, TEXT;
	}

	List<Field> fields;

	public FieldsNode(String id, String idNext) {
		super(id, idNext);
	}

	public FieldsNode addField(String description, FieldType type) {
		Field field = new Field(description, type);
		fields.add(field);
		return this;
	}
}
