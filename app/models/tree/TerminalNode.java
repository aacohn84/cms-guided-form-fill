package models.tree;

import java.util.Map;

import models.data.FilledFormFields;
import play.twirl.api.Html;

public class TerminalNode extends Node {
	public TerminalNode(String id, String description) {
		super(id, description);
	}
	
	@Override
	@SuppressWarnings("unused")
	public void fillFormFields(String serializedObj, FilledFormFields formFields) {

	}

	@Override
	@SuppressWarnings("unused")
	public String getIdNextNode(Map<String, String> input) {
		throw new RuntimeException(
				"Method getIdNextNode not implemented in TerminalNode.");
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	@SuppressWarnings("unused")
	public Html renderAsHtml(String rawInput) {
		return views.html.questionnaire.terminal.render();
	}
	
	@Override
	@SuppressWarnings("unused")
	public String serializeInput(Map<String, String> input) {
		throw new RuntimeException(
				"Method serializeInput not implemented in TerminalNode.");
	}
}
