package models;

import core.forms.CMSForm;

public class FormData {
	private String owner;
	private DecisionTree decisionTree;
	private CMSForm form;
	private Integer rowId;
	
	public FormData(String owner, CMSForm form) {
		this.owner = owner;
		this.form = form;
		this.decisionTree = new DecisionTree(form);
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public String getOwner() {
		return owner;
	}

	public DecisionTree getDecisionTree() {
		return decisionTree;
	}

	public CMSForm getForm() {
		return form;
	}
}
