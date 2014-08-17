package models.data;

public class FormData {
	String owner;
	public DecisionMap decisionMap;
	public FilledFormFields filledFormFields;
	
	public FormData(String owner) {
		this.owner = owner;
		decisionMap = new DecisionMap();
		filledFormFields = new FilledFormFields();
	}
}
