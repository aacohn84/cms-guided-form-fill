package models.tree;

import java.util.Map;

import models.data.FilledFormFields;
import play.twirl.api.Html;

/**
 * A node for displaying a note.
 * 
 * @author Aaron Cohn
 */
public class NoteNode extends SingleTargetNode {
	
	String note;

	public NoteNode(String id, String idNext, String note) {
		super(id, idNext, "Note");
		this.note = note;
	}

	@SuppressWarnings("unused")
	@Override
	public void fillFormFields(String serializedObj,
			FilledFormFields formFields) {
		// NoteNode does not fill any form fields.
	}

	@Override
	protected String getNodeHtml(@SuppressWarnings("unused") String rawInput) {
		return note + "<br>";
	}

	@Override
	public Html renderSelectionAsHtml(String rawInput) {
		return renderAsHtml(rawInput);
	}

	@Override
	public String serializeInput(
			@SuppressWarnings("unused") Map<String, String> input) {
		return "";
	}
}
