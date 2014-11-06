package core.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import core.tree.Node;

public class CMSForm {
	private String formName;
	private LinkedHashMap<String, FormVariable> formVariables = new LinkedHashMap<>();
	private HashMap<String, Node> nodes = new HashMap<>();
	protected Node root;

	public CMSForm(String formName) {
		this.formName = formName;
	}

	protected Node addNode(Node node) {
		if (nodes.containsKey(node.id)) {
			throw new RuntimeException("A node with id " + node.id
					+ " already exists in this form.");
		}
		nodes.put(node.id, node);
		return node;
	}

	public String getFormFileName() {
		return formName + ".pdf";
	}

	public FormVariable getFormVariable(String varId) {
		if (formVariables.containsKey(varId)) {
			return formVariables.get(varId);
		}
		throw new RuntimeException("Variable with ID " + varId
				+ " does not exist in " + formName + " form.");
	}

	public String getFormVariableValue(String varId) {
		if (formVariables.containsKey(varId)) {
			return formVariables.get(varId).getValue();
		}
		throw new RuntimeException("Variable with Id " + varId
				+ " does not exist in " + formName + " form.");
	}

	public String getName() {
		return formName;
	}

	public Node getNode(String nodeId) {
		if (nodes.containsKey(nodeId)) {
			return nodes.get(nodeId);
		}
		throw new RuntimeException("Node with name " + nodeId
				+ " does not exist in " + formName + " form.");
	}

	public Node getRoot() {
		return root;
	}

	protected void setFormVariable(FormVariable formVar) {
		formVariables.put(formVar.getId(), formVar);
	}

	public void setFormVariable(String varId, String varValue) {
		FormVariable fv = formVariables.get(varId);
		if (fv != null) {
			fv.setValue(varValue);
		}
		throw new RuntimeException("Variable with ID " + varId
				+ " does not exist in " + formName + " form.");
	}

	public List<FormVariable> getFormVariables() {
		return new ArrayList<>(formVariables.values());
	}
}
