package models.tree.fields;

import play.twirl.api.Html;

public class HiddenField extends Field {
	public HiddenField(String name, String value) {
		super(name, false, "", value);
	}

	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.hiddenField.render(this);
	}
}