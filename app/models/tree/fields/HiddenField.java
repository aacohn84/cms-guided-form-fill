package models.tree.fields;

import play.twirl.api.Html;

public class HiddenField extends Field {
	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.hiddenField.render(this);
	}
}
