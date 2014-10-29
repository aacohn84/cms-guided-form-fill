package core.tree.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.FilledFormFields;

import org.apache.commons.lang3.StringUtils;

import core.tree.NonBranchingNode;
import play.twirl.api.Html;

public class FieldTableNode extends NonBranchingNode {

	// Simple renaming of Field class
	public static class Column {
		public String baseFieldName;
		public String defaultValue;
		public Class<? extends Field> fieldType;
		public String header;

		public Column(String header, String baseFieldName,
				Class<? extends Field> fieldType, String defaultValue) {
			this.header = header;
			this.baseFieldName = baseFieldName;
			this.fieldType = fieldType;
			this.defaultValue = defaultValue;
		}
	}

	/*
	 * The DisplayRows and DisplayRows classes serve to make it easy to display
	 * a table of fields in a Scala template. This is mainly needed because the
	 * templates don't support traditional for-loops, only for-each loops, which
	 * makes iterating over a matrix-like structure a pain in the ass.
	 */
	public static class DisplayRow {
		public List<Field> fields = new ArrayList<>();

		public void add(Field f) {
			fields.add(f);
		}

		public Field get(int i) {
			return fields.get(i);
		}
	}

	public static class DisplayRows {
		public List<DisplayRow> rows = new ArrayList<>();

		public void add(DisplayRow dc) {
			rows.add(dc);
		}

		public DisplayRow get(int i) {
			return rows.get(i);
		}
	}

	private ArrayList<Column> columns;
	private int numRows;

	public FieldTableNode(String id, String description, int numRows,
			String idNext) {
		super(id, idNext, description, true);
		columns = new ArrayList<Column>();
		if (numRows > 0) {
			this.numRows = numRows;
		} else {
			throw new RuntimeException(
					"FieldTableNode must have at least one row.");
		}
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
	 * @param fieldType
	 *            - Type of field used in this column.
	 * @param defaultValue
	 *            - the value that should be used for fields in this column if
	 *            no value is provided by the user.
	 * @return a reference to the node so that calls to addColumn can be chained
	 *         together.
	 */
	public FieldTableNode addColumn(String header, String baseFieldName,
			Class<? extends Field> fieldType, String defaultValue) {
		for (Column c : columns) {
			if (c.baseFieldName.equals(baseFieldName)) {
				throw new RuntimeException("Column with base field name \""
						+ baseFieldName
						+ "\" already exists in this FieldsTableNode.");
			}
		}
		columns.add(new Column(header, baseFieldName, fieldType, defaultValue));
		return this;
	}

	@Override
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {
		StoredSelection ss = recreateObject(serializedObj,
				StoredSelection.class);
		for (StoredField field : ss.fields) {
			formFields.fillField(field.name, field.value);
		}
	}

	/**
	 * Renders a table of fields.
	 */
	@Override
	public Html renderAsHtml(String rawInput) {
		DisplayRows fieldTable = new DisplayRows();

		// Construct first row (fields in this row are 'required')
		DisplayRow firstRow = new DisplayRow();
		for (Column c : columns) {
			Field field = newField(c.fieldType);
			field.setName(c.baseFieldName + 1);
			field.setRequired(true);
			firstRow.add(field);
		}
		fieldTable.add(firstRow);

		// Construct remaining rows (fields not 'required')
		for (int rowNum = 2; rowNum <= numRows; rowNum++) {
			DisplayRow displayRow = new DisplayRow();
			for (Column c : columns) {
				Field field = newField(c.fieldType);
				field.setName(c.baseFieldName + rowNum);
				// first row is required
				field.setRequired(false);
				displayRow.add(field);
			}
			fieldTable.add(displayRow);
		}

		// fill in values from existing data if possible
		if (StringUtils.isNotEmpty(rawInput)) {
			StoredSelection ss = recreateObject(rawInput, StoredSelection.class);
			if (ss.fields != null) {
				int numCols = columns.size();
				assert (ss.fields.length == numRows * numCols - 1);
				int i = 0;
				for (int col = 0; col < numCols; col++) {
					for (int row = 0; row < numRows; row++) {
						String storedValue = ss.fields[i].value;
						fieldTable.get(row).get(col).value = storedValue;
						i++;
					}
				}
			}
		}
		return views.html.questionnaire.fieldTable.render(fieldTable, columns);
	}

	/**
	 * Store table of fields as a flat array.
	 */
	@Override
	public String serializeInput(Map<String, String> input) {
		ArrayList<StoredField> fields = new ArrayList<>();
		for (Column col : columns) {
			for (int row = 1; row <= numRows; row++) {
				StoredField field = new StoredField();
				field.name = col.baseFieldName + row;
				String inputVal = input.get(field.name);
				if (StringUtils.isEmpty(inputVal)) {
					field.value = col.defaultValue;
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

	private Field newField(Class<? extends Field> fieldType) {
		try {
			return fieldType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
