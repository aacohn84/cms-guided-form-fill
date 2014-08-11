package models.data;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDecisionStore implements DecisionStore {

	static InMemoryDecisionStore instance;

	/**
	 * Produce a singleton instance of InMemoryDecisionStore.
	 */
	public static InMemoryDecisionStore getInstance() {
		if (instance == null) {
			instance = new InMemoryDecisionStore();
		}
		return instance;
	}

	Map<String, DecisionMap> decisionMaps;

	private InMemoryDecisionStore() {
		decisionMaps = new HashMap<String, DecisionMap>();
	}

	@Override
	public boolean containsUsername(String username) {
		return decisionMaps.containsKey(username);
	}

	@Override
	public DecisionMap getDecisions(String username)
			throws NoSuchUserException {
		if (decisionMaps.containsKey(username)) {
			return decisionMaps.get(username);
		}
		throw new NoSuchUserException(username);
	}

	@Override
	public void putDecisions(String username, DecisionMap decisions) {
		decisionMaps.put(username, decisions);
	}

	@Override
	public void removeDecisions(String username) throws NoSuchUserException {
		if (decisionMaps.containsKey(username)) {
			decisionMaps.remove(username);
		}
		throw new NoSuchUserException(username);
	}

}
