package models;

import java.util.HashMap;
import java.util.Map;

public class FormDataStore {

	static FormDataStore instance;
	
	public static FormDataStore getInstance() {
		if (instance == null) {
			instance = new FormDataStore();
		}
		return instance;
	}
	
	Map<String, FormData> formDataStore;

	private FormDataStore() {
		formDataStore = new HashMap<String, FormData>();
	}
	
	public boolean containsUsername(String username) {
		return formDataStore.containsKey(username);
	}

	public FormData getFormData(String username) throws NoSuchUserException {
		if (formDataStore.containsKey(username)) {
			return formDataStore.get(username);
		}
		throw new NoSuchUserException(username);
	}

	public void setFormData(String username, FormData formData) {
		formDataStore.put(username, formData);
	}

	public void removeFormData(String username) throws NoSuchUserException {
		if (formDataStore.containsKey(username)) {
			formDataStore.remove(username);
		} else {
			throw new NoSuchUserException(username);
		}
	}

}
