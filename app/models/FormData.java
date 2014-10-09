package models;

import core.forms.CMSForm;
import core.tree.Node;

public class FormData {
	private DecisionTree decisionTree;
	private CMSForm form;
	private String owner; // name of the employee...
	private Integer rowId;

	public FormData(String owner, CMSForm form) {
		this.owner = owner;
		this.form = form;
		this.decisionTree = new DecisionTree(form);
	}

	public DecisionTree getDecisionTree() {
		return decisionTree;
	}

	/**
	 * Returns filled form fields along the active path in the DecisionTree.
	 */
	public FilledFormFields getFilledFormFields() {
		FilledFormFields fields = new FilledFormFields();
		for (Decision decision : decisionTree) {
			Node context = decision.context;
			if (context.isOutputNode) {
				context.fillFormFields(decision.serializedInput, fields);
			}
		}
		return fields;
	}

	public CMSForm getForm() {
		return form;
	}

	public String getOwner() {
		return owner;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}
}
