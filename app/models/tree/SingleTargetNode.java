package models.tree;

import java.util.Map;

public class SingleTargetNode extends Node {

	String idNext;
	
	public SingleTargetNode(String id, String idNext) {
		super(id);
		this.idNext = idNext;
	}

	@Override
	public String idNextNode(@SuppressWarnings("unused") Map<String, String> input) {
		return idNext;
	}

}
