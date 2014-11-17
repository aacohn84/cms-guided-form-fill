package core.tree.fields;

import play.twirl.api.Html;

public class TextAreaField extends Field {
	private int numRows;
	private int numCols;

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public TextAreaField(String name, boolean isRequired, String label,
			int numRows, int numCols) {
		super(name, isRequired, label);
		this.numRows = numRows;
		this.numCols = numCols;
	}

	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.textAreaField.render(this);
	}
}