package models.data;

public interface DecisionStore {

	/**
	 * Returns <code>true</code> if the specified username exists in the
	 * DecisionStore, otherwise <code>false</code>.
	 */
	public boolean containsUsername(String username);

	/**
	 * Retrieve a DecisionQueue from the DecisionStore.
	 * 
	 * @param username
	 *            - the name of the logged-in user who stored the DecisionQueue.
	 * @throws NoSuchUserException
	 *             if the specified username doesn't match any entries within
	 *             the DecisionStore.
	 */
	public DecisionQueue getDecisionQueue(String username)
			throws NoSuchUserException;

	/**
	 * Inserts or updates the specified DecisionQueue in the DecisionStore, with
	 * the specified username as the key.
	 * 
	 * @param username
	 *            - the key under which the DecisionQueue will be stored.
	 * @param decisionQueue
	 *            - the DecisionQueue to be inserted or updated.
	 */
	public void putDecisionQueue(String username, DecisionQueue decisionQueue);

	/**
	 * Removes the DecisionQueue associated with the specified username.
	 * 
	 * @param username
	 *            - the user whose DecisionQueue should be removed.
	 * 
	 * @throws NoSuchUserException
	 *             if the specified username doesn't match any entries within
	 *             the DecisionStore.
	 */
	public void removeDecisionQueue(String username) throws NoSuchUserException;
}
