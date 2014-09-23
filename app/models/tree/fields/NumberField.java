package models.tree.fields;

import play.twirl.api.Html;

public class NumberField extends Field {
	public NumberField() {}
	
	public NumberField(String name, boolean isRequired, String label) {
		super(name, isRequired, label);
	}

	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.numberField.render(this);
	}
}