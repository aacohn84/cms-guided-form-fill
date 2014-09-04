package models.tree;

import java.util.Map;

import models.data.FilledFormFields;
import play.twirl.api.Html;

public class TerminalNode extends Node {
	public TerminalNode(String id, String description) {
		super(id, description);
	}
	
	@Override
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {

	}

	@Override
	public String getIdNextNode(Map<String, String> input) {
		return null;
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public Html renderAsHtml(String rawInput) {
		return views.html.questionnaire.terminal.render();
	}
	
	@Override
	public String serializeInput(Map<String, String> input) {
		return null;
	}
}
