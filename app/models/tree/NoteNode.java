package models.tree;

import java.util.Map;

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
	protected String getNodeHtml() {
		return description;
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		return "";
	}
}
