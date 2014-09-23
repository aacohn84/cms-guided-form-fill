package models.tree.fields;

import play.twirl.api.Html;

public class TextField extends Field {
	public TextField() {}
	
	public TextField(String name, boolean isRequired, String label) {
		super(name, isRequired, label);
	}

	public TextField(String name, boolean isRequired, String label,
			String defaultValue) {
		super(name, isRequired, label, defaultValue);
	}

	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.textField.render(this);
	}
}
