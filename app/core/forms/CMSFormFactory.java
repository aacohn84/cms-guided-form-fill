package core.forms;

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
		switch(formName) {
		case ChangeOrderForm.NAME:
			ChangeOrderForm.reset();
		}
	}
}
