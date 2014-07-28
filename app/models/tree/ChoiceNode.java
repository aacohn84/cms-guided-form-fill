package models.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import play.twirl.api.Html;

/**
 * A node with some mutually exclusive options to choose from, intended to be
 * implemented with radio-buttons in HTML.
 * 
 * @author Aaron Cohn
 */
public class ChoiceNode extends Node {

	/**
	 * A mapping of each option to the identifier of its target node.
	 */
	Map<String, String> options;

	public ChoiceNode(String id) {
		super(id);
	}

	/**
	 * Adds an option that points to the target node.
	 * 
	 * @param option
	 *            - text describing the option.
	 * @param targetId
	 *            - id of the node corresponding to the option.
	 */
	public ChoiceNode addOption(String option, String targetId) {
		options.put(option, targetId);
		return this;
	}

	/**
	 * Returns a list of options to choose from.
	 */
	public List<String> getOptions() {
		return new ArrayList<String>(options.keySet());
	}

	@Override
	public String idNextNode(Map<String, String> input) {
		String choice = input.get("choice");
		return options.get(choice);
	}

	@Override
	public Html renderAsHtml() {
		String html = new String();
		for (Entry<String, String> option : options.entrySet()) {
			html += "<input type=\"radio\" name=\"choice\" value=\""
					+ option.getKey() + "\">" + option.getKey() + "<br>";
		}
		return new Html(html);
	}
}
