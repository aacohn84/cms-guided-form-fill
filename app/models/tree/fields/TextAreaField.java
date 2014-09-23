package models.tree.fields;

import play.twirl.api.Html;

public class TextAreaField extends Field {
	public TextAreaField(String name, boolean isRequired, String label) {
		super(name, isRequired, label);
	}

	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.textAreaField.render(this);
	}
}