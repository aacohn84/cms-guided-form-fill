package core.tree;

import java.util.Map;

import models.FilledFormFields;
import play.twirl.api.Html;

/**
 * Like a {@link NoteNode} but also checks a box on the form after it is
 * visited.
 */
public class NoteChecksABoxNode extends NonBranchingNode {

	private String boxName;
	private String note;

	public NoteChecksABoxNode(String id, String idNext, String boxName,
			String note) {
		super(id, idNext, "Note:", true);
		this.boxName = boxName;
		this.note = note;
	}

	@Override
	public void fillFormFields(
			@SuppressWarnings("unused") String serializedObj,
			FilledFormFields formFields) {
		formFields.fillField(boxName, "Yes");
	}

	@Override
	@SuppressWarnings("unused")
	public String serializeInput(Map<String, String> input) {
		return "";
	}

	@Override
	@SuppressWarnings("unused")
	public Html renderAsHtml(String rawInput) {
		return views.html.questionnaire.note.render(note);
	}
}
