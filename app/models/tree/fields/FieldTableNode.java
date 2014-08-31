package models.tree.fields;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import models.data.FilledFormFields;
import models.tree.SingleTargetNode;
import play.twirl.api.Html;

public class FieldTableNode extends SingleTargetNode {

	// Simple renaming of Field class
	private static class Column extends Field {
		public Column(String header, String baseFieldName, FieldType type) {
			super(header, baseFieldName, type);
		}
	}
	
	private ArrayList<Column> columns;
	private int numRows;
	
	public FieldTableNode(String id, String description, int numRows,
			String idNext) {
		super(id, idNext, description, true);
		columns = new ArrayList<Column>();
		this.numRows = numRows;
	}

	/**
	 * Add a column to the field table.
	 * 
	 * @param header
	 *            - Text to display at the head of the column.
	 * @param baseFieldName
	 *            - Base form-field name for this column. Numbers will be
	 *            appended to it in increasing order. For example, if the name
	 *            given is "Item Code ", then each field in the column will get
	 *            a name like, "Item Code 1", "Item Code 2", ..., "Item Code N".
	 * @param type
	 *            - Type of input accepted by fields in this column.
	 * @return a reference to the FieldsNode so that calls to addColumn can be
	 *         chained together.
	 */
	public FieldTableNode addColumn(String header, String baseFieldName,
			FieldType type) {
		for (Column c : columns) {
			if (c.name.equals(baseFieldName)) {
				throw new RuntimeException("Column with base field name \""
						+ baseFieldName
						+ "\" already exists in this FieldsTableNode.");
			}
		}
		columns.add(new Column(header, baseFieldName, type));
		return this;
	}

	@Override
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {
		StoredSelection ss = (StoredSelection) recreateObject(serializedObj);
		for (StoredSelection.Field field : ss.fields) {
			formFields.fillField(field.name, field.value);
		}
	}

	@Override
	public Html renderSelectionAsHtml(String serializedSelection) {
		return new Html("");
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		ArrayList<StoredSelection.Field> fields = new ArrayList<>();
		for (Column col : columns) {
			for (int row = 1; row <= numRows; row++) {
				StoredSelection.Field field = new StoredSelection.Field();
				field.name = col.name + row;
				String inputVal = input.get(field.name);
				if (StringUtils.isEmpty(inputVal)) {
					field.value = col.type.defaultVal();
				} else {
					field.value = inputVal;
				}
				fields.add(field);
			}
		}
		StoredSelection ss = new StoredSelection();
		ss.fields = new StoredSelection.Field[fields.size()];
		fields.toArray(ss.fields);
		return serializeAsString(ss);
	}

	@Override
	protected String getNodeHtml(String rawInput) {
		// build header row
		String html = "<table><tr>";
		for (Column c : columns) {
			html += "<th>" + c.description + "</th>";
		}
		html += "</tr>\n";
		
		// build rows of input fields
		for (int row = 0; row < numRows; row++) {
			html += "<tr>";
			int numCols = columns.size();
			for (int col = 0; col < numCols; col++) {
				Column c = columns.get(col);
				html += "<td>" + "<input type=\"" + c.type.toString()
						+ "\" name=\"" + c.name + (row + 1) + "\"" + "></td>";
			}
			html += "</tr>\n";
		}
		html += "</table>\n";
		
		return html;
	}

}
