package models.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import models.tree.Node;

public class CMSForm {
	private String formFileName;
	private HashMap<String, Node> nodes = new HashMap<>();
	protected Node root;

	public CMSForm(String formFileName) {
		this.formFileName = formFileName;
	}

	public String getFormFileName() {
		return formFileName;
	}

	public Node getNode(String nodeId) {
		if (nodes.containsKey(nodeId)) {
			return nodes.get(nodeId);
		}
		throw new RuntimeException("Node with name " + nodeId
				+ " does not exist in this form.");
	}

	/**
	 * Returns a list nodes for which the <code>isOutputNode</code> field is set
	 * to <code>true</code>.
	 */
	public List<Node> getOutputNodes() {
		List<Node> outputNodes = new ArrayList<>();
		for (Entry<String, Node> entry : nodes.entrySet()) {
			Node node = entry.getValue();
			if (node.isOutputNode) {
				outputNodes.add(node);
			}
		}
		return outputNodes;
	}

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
}
