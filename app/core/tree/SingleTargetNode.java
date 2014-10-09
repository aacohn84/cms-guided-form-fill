package core.tree;

import java.util.Map;

/**
 * A {@link Node} with only one possibility for the ID of the next node in the
 * form. Meaning, the node ID you get from idNextNode is not based on some
 * decision or input, it's always the same node, regardless of what input is
 * given.
 * 
 * @author Aaron Cohn
 */
public abstract class SingleTargetNode extends Node {

	String idNext;

	public SingleTargetNode(String id, String idNext, String description) {
		super(id, description);
		this.idNext = idNext;
	}

	public SingleTargetNode(String id, String idNext, String description,
			boolean isOutputNode) {
		super(id, description, isOutputNode);
		this.idNext = idNext;
	}
	
	public SingleTargetNode(String id, String idNext, String description,
			boolean isOutputNode, boolean isVisible) {
		super(id, description, isOutputNode, isVisible);
		this.idNext = idNext;
	}

	/**
	 * Returns the identifier of the next node regardless of input entered.
	 */
	@Override
	public String getIdNextNode(
			@SuppressWarnings("unused") Map<String, String> input) {
		return idNext;
	}

}