package core.forms;

import java.util.Map;

public class CMSFormFactory {
	public static CMSForm getForm(String formName) {
		switch (formName) {
		case ChangeOrderForm.NAME:
			return ChangeOrderForm.getInstance();
		}
		throw new RuntimeException("Form with name " + formName
				+ " does not exist.");
	}

	public static void resetForm(String formName) {
		switch (formName) {
		case ChangeOrderForm.NAME:
			ChangeOrderForm.reset();
			break;
		}
	}

	public static void updateFormVariables(String formName,
			Map<String, String> requestData) {
		switch (formName) {
		case ChangeOrderForm.NAME:
			ChangeOrderForm.updateFormVariables(requestData);
		}
	}
}
