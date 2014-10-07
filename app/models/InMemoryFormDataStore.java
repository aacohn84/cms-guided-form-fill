package models;

import java.util.HashMap;
import java.util.Map;

public class InMemoryFormDataStore implements FormDataStore {

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
	
	@Override
	public boolean containsUsername(String username) {
		return formDataStore.containsKey(username);
	}

	@Override
	public FormData getFormData(String username) throws NoSuchUserException {
		if (formDataStore.containsKey(username)) {
			return formDataStore.get(username);
		}
		throw new NoSuchUserException(username);
	}

	@Override
	public void setFormData(String username, FormData formData) {
		formDataStore.put(username, formData);
	}

	@Override
	public void removeFormData(String username) throws NoSuchUserException {
		if (formDataStore.containsKey(username)) {
			formDataStore.remove(username);
		} else {
			throw new NoSuchUserException(username);
		}
	}

}
