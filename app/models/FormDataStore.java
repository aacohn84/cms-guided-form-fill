package models;

public interface FormDataStore {
	/**
	 * Returns <code>true</code> if the specified username is associated with a
	 * FormData in the FormDataStore, otherwise <code>false</code>.
	 */
	public boolean containsUsername(String username);

	/**
	 * Retrieve FormData from the FormDataStore.
	 * 
	 * @param username
	 *            - the name of the logged-in user who stored the FormData.
	 * @throws NoSuchUserException
	 *             if the specified username doesn't match any entries within
	 *             the FormDataStore.
	 */
	public FormData getFormData(String username) throws NoSuchUserException;

	/**
	 * Inserts or updates the specified FormData in the FormDataStore, with the
	 * specified username as the key.
	 * 
	 * @param username
	 *            - the key under which the FormData will be stored.
	 * @param formData
	 *            - the FormData to be inserted or updated.
	 */
	public void setFormData(String username, FormData formData);

	/**
	 * Removes the FormData associated with the specified username.
	 * 
	 * @param username
	 *            - the user whose FormData should be removed.
	 * 
	 * @throws NoSuchUserException
	 *             if the specified username doesn't match any entries within
	 *             the FormDataStore.
	 */
	public void removeFormData(String username) throws NoSuchUserException;
}
