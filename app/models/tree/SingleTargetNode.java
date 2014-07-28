package models.tree;

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
	
	public SingleTargetNode(String id, String idNext) {
		super(id);
		this.idNext = idNext;
	}

	/**
	 * Returns the identifier of the next node regardless of input entered.
	 */
	@Override
	public String idNextNode(
			@SuppressWarnings("unused") Map<String, String> input) {
		return idNext;
	}

}
