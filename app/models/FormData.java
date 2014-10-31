package models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import play.Logger;
import core.CMSGuidedFormFill;
import core.forms.CMSForm;
import core.tree.Node;

public class FormData {
	private DecisionTree decisionTree;
	private CMSForm form;
	private String employeeName;
	private int employeeId;
	private Integer formRowId;
	private PatronInfo patron1;
	private PatronInfo patron2 = PatronInfo.EMPTY_PATRON_INFO;

	public FormData(String employeeName, int employeeId, CMSForm form) {
		this.employeeName = employeeName;
		this.employeeId = employeeId;
		this.form = form;
		this.decisionTree = new DecisionTree(form);
	}

	public DecisionTree getDecisionTree() {
		return decisionTree;
	}

	/**
	 * Returns filled form fields along the active path in the DecisionTree.
	 */
	public FilledFormFields getFilledFormFields() {
		FilledFormFields fields = new FilledFormFields();
		for (Decision decision : decisionTree) {
			Node context = decision.context;
			if (context.isOutputNode) {
				context.fillFormFields(decision.serializedInput, fields);
			}
		}
		return fields;
	}

	public CMSForm getForm() {
		return form;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public Integer getFormRowId() {
		return formRowId;
	}

	public void setFormRowId(Integer rowId) {
		this.formRowId = rowId;
	}

	/**
	 * Saves filled form fields to database and decision tree to file.
	 */
	public void saveToDisk() {
		FilledFormFields filledFormFields = getFilledFormFields();
		String formName = form.getName();
		if (formRowId == null) {
			patron1 = createPatron1(filledFormFields);
			patron2 = createPatron2IfPossible(filledFormFields);
			formRowId = CMSDB.newForm(formName, employeeId,
					patron1.getPatronId(), patron2.getPatronId(),
					filledFormFields);
			Logger.info("New " + formName + " created with formRowId: "
					+ formRowId);
		} else {
			updatePatron1IfNecessary(filledFormFields);
			updatePatron2IfNecessary(filledFormFields);
			CMSDB.updateForm(formName, formRowId, patron2.getPatronId(),
					filledFormFields);
		}
		writeFormDataToFile();
	}

	private PatronInfo createPatron1(FilledFormFields filledFormFields) {
		PatronInfo patron = PatronInfo.getPatron1Info(filledFormFields);
		patron.setPatronId(CMSDB.createPatron(patron));
		return patron;
	}

	private PatronInfo createPatron2IfPossible(FilledFormFields filledFormFields) {
		if (filledFormFields.isFieldFilled("name_2")) {
			PatronInfo patron = PatronInfo.getPatron2Info(filledFormFields);
			patron.setPatronId(CMSDB.createPatron(patron));
			return patron;
		}
		return PatronInfo.EMPTY_PATRON_INFO;
	}

	private void updatePatron1IfNecessary(FilledFormFields filledFormFields) {
		PatronInfo newPatronInfo = PatronInfo
				.getPatron1Info(filledFormFields);
		if (!patron1.equals(newPatronInfo)) {
			newPatronInfo.setPatronId(patron1.getPatronId());
			patron1 = newPatronInfo;
			CMSDB.updatePatron(newPatronInfo);
		}
	}

	private void updatePatron2IfNecessary(FilledFormFields filledFormFields) {
		if (patron2.getPatronId() == null) {
			patron2 = createPatron2IfPossible(filledFormFields);
		} else {
			// patron 2 exists, let's see if it should be updated...
			if (filledFormFields.isFieldFilled("name_2")) {
				PatronInfo newPatronInfo = PatronInfo
						.getPatron2Info(filledFormFields);
				if (!patron2.equals(newPatronInfo)) {
					newPatronInfo.setPatronId(patron2.getPatronId());
					patron2 = newPatronInfo;
					CMSDB.updatePatron(newPatronInfo);
				}
			} else {
				patron2 = PatronInfo.EMPTY_PATRON_INFO;
			}
		}
	}

	/*
	 * Writes a file containing the entire decision tree. The filename is based
	 * on the form name and the row id. So if it's the Change Order form and row
	 * 2, the filename will be change_order2.decisions
	 */
	private void writeFormDataToFile() {
		String formName = form.getName();
		String filename = getFormDataFileName(formName, formRowId);
		File formDataFile = new File(CMSGuidedFormFill.getFormDataFile(),
				filename);
		try (PrintWriter formDataWriter = new PrintWriter(formDataFile)) {
			formDataWriter.write(decisionTree.serialize());
			formDataWriter.write("\n");
			formDataWriter.write(patron1.serialize());
			formDataWriter.write("\n");
			formDataWriter.write(patron2.serialize());
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Constructs a FormData instance from the serialized decisions file stored
	 * on disk. The form and the formRowId form the key by which the data is
	 * stored and accessed.
	 *
	 * @param form
	 *            - Instance of the form associated with the data being
	 *            accessed.
	 * @param employeeName
	 *            - name of the employee who entered the data.
	 * @param employeeId
	 *            - the database id of the employee
	 * @param formRowId
	 *            - the id of the form instance loaded
	 * @return a new FormData instance loaded from disk.
	 */
	public static FormData loadFromDisk(CMSForm form, String employeeName,
			int employeeId, int rowId) {
		String filename = getFormDataFileName(form.getName(), rowId);
		File decisionFile = new File(CMSGuidedFormFill.getFormDataFile(),
				filename);
		String serializedDecisions = null;
		String serializedPatron1 = null;
		String serializedPatron2 = null;
		try (BufferedReader decisionReader = new BufferedReader(
				new FileReader(decisionFile));) {
			serializedDecisions = decisionReader.readLine();
			serializedPatron1 = decisionReader.readLine();
			serializedPatron2 = decisionReader.readLine();
		} catch (IOException e) {
			throw new RuntimeException(
					"Could not read serialized form data in " + filename, e);
		}
		FormData formData = new FormData(employeeName, employeeId, form);
		formData.decisionTree.deserialize(serializedDecisions);
		formData.patron1 = PatronInfo.deserialize(serializedPatron1);
		formData.patron2 = PatronInfo.deserialize(serializedPatron2);
		formData.formRowId = rowId;
		return formData;
	}

	private static String getFormDataFileName(String formName, int rowId) {
		String filename = formName + rowId + ".formdata";
		return filename;
	}
}
