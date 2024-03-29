package core.forms;

import java.util.HashMap;

import core.tree.Node;

public class CMSForm {
	private String formName;
	private HashMap<String, Node> nodes = new HashMap<>();
	protected Node root;

	public CMSForm(String formName) {
		this.formName = formName;
	}

	public String getFormFileName() {
		return formName + ".pdf";
	}

	public String getName() {
		return formName;
	}

	public Node getNode(String nodeId) {
		if (nodes.containsKey(nodeId)) {
			return nodes.get(nodeId);
		}
		throw new RuntimeException("Node with name " + nodeId
				+ " does not exist in this form.");
	}

	public Node getRoot() {
		return root;
	}

	protected Node addNode(Node node) {
		if (nodes.containsKey(node.id)) {
			throw new RuntimeException("A node with id " + node.id
					+ " already exists in this form.");
		}
		nodes.put(node.id, node);
		return node;
	}
}
