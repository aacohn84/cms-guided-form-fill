package models.tree;

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

	@Override
	public Html renderAsHtml() {
		return new Html(description);
	}
}
