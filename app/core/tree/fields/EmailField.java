package core.tree.fields;

import play.twirl.api.Html;

public class EmailField extends Field {
	public EmailField(String name, boolean isRequired, String label) {
		super(name, isRequired, label);
	}
	
	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.emailField.render(this);
	}
}
