package models.tree.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.data.FilledFormFields;
import models.tree.SingleTargetNode;

import org.apache.commons.lang3.StringUtils;

import play.twirl.api.Html;

public class FieldTableNode extends SingleTargetNode {

	// Simple renaming of Field class
	public static class Column {
		public String header;
		public String baseFieldName;
		public FieldType fieldType;
		
		public Column(String header, String baseFieldName, FieldType fieldType) {
			this.header = header;
			this.baseFieldName = baseFieldName;
			this.fieldType = fieldType;
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
			if (c.baseFieldName.equals(baseFieldName)) {
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
		StoredSelection ss = recreateObject(serializedObj, StoredSelection.class);
		for (StoredField field : ss.fields) {
			formFields.fillField(field.name, field.value);
		}
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		ArrayList<StoredField> fields = new ArrayList<>();
		for (Column col : columns) {
			for (int row = 1; row <= numRows; row++) {
				StoredField field = new StoredField();
				field.name = col.baseFieldName + row;
				String inputVal = input.get(field.name);
				if (StringUtils.isEmpty(inputVal)) {
					field.value = col.fieldType.defaultVal();
				} else {
					field.value = inputVal;
				}
				fields.add(field);
			}
		}
		StoredSelection ss = new StoredSelection();
		ss.fields = new StoredField[fields.size()];
		fields.toArray(ss.fields);
		return serializeAsString(ss);
	}

	/*
	 * Returns a list of integers from 1..numRows. This workaround brought to
	 * you by the irritating lack of support for counting loops in Scala
	 * templates.
	 */
	private List<Integer> getRowNumbers() {
		ArrayList<Integer> rows = new ArrayList<>(numRows);
		for (int i = 1; i <= numRows; i++) {
			rows.add(i);
		}
		return rows;
	}

	@Override
	public Html renderAsHtml(String rawInput) {
		return views.html.questionnaire.fieldTable.render(getRowNumbers(),
				columns);
	}
}
