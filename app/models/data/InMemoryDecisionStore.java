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

	Map<String, DecisionQueue> decisionQueues;

	private InMemoryDecisionStore() {
		decisionQueues = new HashMap<String, DecisionQueue>();
	}

	@Override
	public boolean containsUsername(String username) {
		return decisionQueues.containsKey(username);
	}

	@Override
	public DecisionQueue getDecisionQueue(String username)
			throws NoSuchUserException {
		if (decisionQueues.containsKey(username)) {
			return decisionQueues.get(username);
		}
		throw new NoSuchUserException(username);
	}

	@Override
	public void putDecisionQueue(String username, DecisionQueue decisionQueue) {
		decisionQueues.put(username, decisionQueue);
	}

	@Override
	public void removeDecisionQueue(String username) throws NoSuchUserException {
		if (decisionQueues.containsKey(username)) {
			decisionQueues.remove(username);
		}
		throw new NoSuchUserException(username);
	}

}
