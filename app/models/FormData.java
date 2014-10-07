package models;

public class FormData {
	String owner;
	public DecisionMap decisionMap;
	
	public FormData(String owner) {
		this.owner = owner;
		this.decisionMap = new DecisionMap();
	}
}
