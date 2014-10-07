package core.tree.fields;

import java.util.Arrays;
import java.util.List;

import play.twirl.api.Html;

public class SelectField extends Field {
	public List<String> options;

	public SelectField(String name, boolean isRequired, String label,
			String[] options) {
		super(name, isRequired, label);
		this.options = Arrays.asList(options);
	}

	@Override
	public Html renderAsHtml() {
		return views.html.questionnaire.fieldTypes.selectField.render(this);
	}
}