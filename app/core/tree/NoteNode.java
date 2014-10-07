package core.tree;

import java.util.Map;

import models.FilledFormFields;
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
	@SuppressWarnings("unused")
	public Html renderAsHtml(String rawInput) {
		return views.html.questionnaire.note.render(note);
	}
	
	@Override
	public String serializeInput(
			@SuppressWarnings("unused") Map<String, String> input) {
		return "";
	}
}
