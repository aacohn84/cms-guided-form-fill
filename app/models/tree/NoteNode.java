package models.tree;

/**
 * A node for displaying a note.
 * 
 * @author Aaron Cohn
 */
public class NoteNode extends SingleTargetNode {
	String message;

	public NoteNode(String id, String message, String idNext) {
		super(id, idNext);
		this.message = message;
	}
}
