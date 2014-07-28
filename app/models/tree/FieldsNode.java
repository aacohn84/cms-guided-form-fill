package models.tree;

import java.util.ArrayList;
import java.util.List;

import play.twirl.api.Html;

/**
 * A node with fields to fill in. Each field has a description and a type. The
 * fields are meant to be rendered as inputs in HTML.
 * 
 * @author Aaron Cohn
 */
public class FieldsNode extends SingleTargetNode {
	public static class Field {
		String description;
		String name;
		FieldType type;

		public Field(String description, FieldType type) {
			this.description = description;
			this.type = type;
		}
	}

	public static enum FieldType {
		NUMBER, TEXT;
	}

	List<Field> fields = new ArrayList<>();

	public FieldsNode(String id, String idNext) {
		super(id, idNext);
	}

	public FieldsNode addField(String description, FieldType type) {
		Field field = new Field(description, type);
		fields.add(field);
		return this;
	}

	@Override
	public Html renderAsHtml() {
		String html = new String();
		for (Field field : fields) {
			String type = field.type.toString();
			html += field.description + ": <input type=\"" + type
					+ "\" name=\"" + field.name + "\"><br>";
		}
		return new Html(html);
	}
}
