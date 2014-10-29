package core.tree;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import models.FilledFormFields;
import play.twirl.api.Html;

public class FeeNode extends NonBranchingNode {
	public static class StoredSelection implements Serializable {
		private static final long serialVersionUID = 4534391046499143699L;
		public String fieldName;
		public String fieldVal;
	}

	BigDecimal fee;
	String fieldName;
	String note;

	public FeeNode(String id, String idNext, String note, String fieldName,
			BigDecimal fee) {
		super(id, idNext, "Note:", true);
		this.note = note;
		this.fieldName = fieldName;
		this.fee = fee;
	}

	@Override
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {
		StoredSelection ss = recreateObject(serializedObj, StoredSelection.class);
		formFields.fillField(ss.fieldName, ss.fieldVal);
	}

	@Override
	@SuppressWarnings("unused")
	public Html renderAsHtml(String rawInput) {
		return views.html.questionnaire.note.render(note);
	}

	@Override
	@SuppressWarnings("unused")
	public String serializeInput(Map<String, String> input) {
		StoredSelection ss = new StoredSelection();
		ss.fieldName = fieldName;
		ss.fieldVal = fee.toString();
		return serializeAsString(ss);
	}
}
