package models.tree;

import java.util.Map;

import play.twirl.api.Html;

/**
 * A node for displaying a note.
 * 
 * @author Aaron Cohn
 */
public class NoteNode extends SingleTargetNode {

	public NoteNode(String id, String idNext, String note) {
		super(id, idNext, note);
	}

	@SuppressWarnings("unused")
	@Override
	public void fillFormFields(String serializedObj,
			Map<String, String> formFields) {
		// NoteNode does not fill any form fields.
	}

	@Override
	protected String getNodeHtml() {
		return description;
	}

	@Override
	public Html renderSelectionAsHtml(
			@SuppressWarnings("unused") String serializedSelection) {
		return renderAsHtml();
	}

	@Override
	public String serializeInput(
			@SuppressWarnings("unused") Map<String, String> input) {
		return "";
	}
}
