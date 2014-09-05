package models.tree.fields;

import play.twirl.api.Html;

public class NumberField extends Field {
	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.numberField.render(this);
	}
}
