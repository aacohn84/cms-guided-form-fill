package models;

import core.forms.CMSForm;

public class FormData {
	String owner;
	public DecisionTree decisionTree;
	public Integer rowId;
	
	public FormData(String owner, CMSForm form) {
		this.owner = owner;
		this.decisionTree = new DecisionTree(form);
	}
}
