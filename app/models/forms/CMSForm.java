package models.forms;

import java.util.HashMap;

import models.tree.Node;

public class CMSForm {
	private HashMap<String, Node> nodes = new HashMap<>();
	protected Node root;

	public Node getRoot() {
		return root;
	}

	void addNode(Node node) {
		if (nodes.containsKey(node.id)) {
			throw new RuntimeException("A node with the name " + node.id
					+ " already exists in this form.");
		}
		nodes.put(node.id, node);
	}

	public Node getNode(String nodeId) {
		if (nodes.containsKey(nodeId)) {
			return nodes.get(nodeId);
		}
		throw new RuntimeException("Node with name " + nodeId
				+ " does not exist in this form.");
	}
}
