package models.tree;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import play.twirl.api.Html;
import models.data.FilledFormFields;

public class FeeNode extends SingleTargetNode {
	static class StoredSelection implements Serializable {
		private static final long serialVersionUID = 4534391046499143699L;
		String fieldName;
		String fieldVal;
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
		StoredSelection ss = (StoredSelection) recreateObject(serializedObj);
		formFields.fillField(ss.fieldName, ss.fieldVal);
	}

	@Override
	public Html renderSelectionAsHtml(String serializedSelection) {
		StoredSelection ss = (StoredSelection) recreateObject(serializedSelection);
		String html = "<strong>" + ss.fieldName + ": </strong>" + ss.fieldVal;
		return new Html(html);
	}

	@Override
	public String serializeInput(
			@SuppressWarnings("unused") Map<String, String> input) {
		StoredSelection ss = new StoredSelection();
		ss.fieldName = fieldName;
		ss.fieldVal = fee.toString();
		return serializeAsString(ss);
	}

	@Override
	protected String getNodeHtml(@SuppressWarnings("unused") String rawInput) {
		return note + "<br>";
	}
}
