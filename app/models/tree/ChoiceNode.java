package models.tree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A node with some mutually exclusive options to choose from, intended to be
 * implemented with radio-buttons in HTML.
 * 
 * @author Aaron Cohn
 */
public class ChoiceNode extends Node {

	/**
	 * A data structure for storing the user's selection, intended to be
	 * serialized and stored as a String.
	 * 
	 * @author Aaron Cohn
	 */
	private static class StoredSelection implements Serializable {
		private static final long serialVersionUID = 3979783495632227041L;

		public String choice;

		public StoredSelection(String choice) {
			this.choice = choice;
		}
	}

	/**
	 * A mapping of each option to the identifier of its target node.
	 */
	Map<String, String> options = new HashMap<>();

	public ChoiceNode(String id, String description) {
		super(id, description);
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
	public String getIdNextNode(Map<String, String> input) {
		String choice = input.get("choice");
		return options.get(choice);
	}

	@Override
	protected String getNodeHtml() {
		String html = new String();
		for (Entry<String, String> option : options.entrySet()) {
			html += "<input type=\"radio\" name=\"choice\" value=\""
					+ option.getKey() + "\">" + option.getKey() + "<br>";
		}
		return html;
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		String choice = input.get("choice");
		StoredSelection storedSelection = new StoredSelection(choice);
		return serializeAsString(storedSelection);
	}

}
