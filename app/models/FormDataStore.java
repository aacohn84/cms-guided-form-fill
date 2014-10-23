package models;

import java.util.HashMap;
import java.util.Map;

import play.Logger;
import core.forms.CMSForm;

public class FormDataStore {
	private static FormDataStore instance;

	private static String getFormDataKey(String cmsFormName, String employeeName) {
		return cmsFormName + employeeName;
	}

	public static FormDataStore getInstance() {
		if (instance == null) {
			instance = new FormDataStore();
		}
		return instance;
	}

	private Map<String, FormData> formDataStore;

	private FormDataStore() {
		formDataStore = new HashMap<String, FormData>();
	}

	public boolean containsEntry(String formName, String employeeName) {
		String formDataKey = getFormDataKey(formName, employeeName);
		return formDataStore.containsKey(formDataKey);
	}

	public FormData getFormData(String formName, String employeeName) {
		return formDataStore.get(getFormDataKey(formName, employeeName));
	}

	public FormData removeFormData(String formName, String employeeName) {
		return formDataStore.remove(getFormDataKey(formName, employeeName));
	}

	public void saveFormData(FormData formData, String employeeName) {
		String formName = formData.getForm().getName();
		Logger.info("Saving " + formName + " for " + employeeName);
		if (formData.getDecisionTree().isComplete()) {
			Logger.info("Decision tree is complete, saving to disk.");
			formData.saveToDisk();
		}
		// save FormData to memory
		String formDataKey = getFormDataKey(formName, employeeName);
		if (!formDataStore.containsKey(formDataKey)) {
			Logger.info("New " + formName + " entry for " + employeeName);
			formDataStore.put(formDataKey, formData);
		}
	}

	public void loadFormData(CMSForm cmsForm, String employeeName,
			int employeeId, int rowId) {
		FormData formData = FormData.loadFromDisk(cmsForm, employeeName, employeeId, rowId);
		String formDataKey = getFormDataKey(formData.getForm().getName(),
				formData.getEmployeeName());
		formDataStore.put(formDataKey, formData);
	}
}
