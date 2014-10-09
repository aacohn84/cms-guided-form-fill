package models;

import java.util.HashMap;
import java.util.Map;

public class InMemoryFormDataStore {

	static InMemoryFormDataStore instance;
	
	public static InMemoryFormDataStore getInstance() {
		if (instance == null) {
			instance = new InMemoryFormDataStore();
		}
		return instance;
	}
	
	Map<String, FormData> formDataStore;

	private InMemoryFormDataStore() {
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
