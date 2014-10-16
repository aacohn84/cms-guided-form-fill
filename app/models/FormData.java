package models;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import play.Logger;
import play.db.DB;
import models.FilledFormFields.FilledFormField;
import core.CMSGuidedFormFill;
import core.forms.CMSForm;
import core.tree.Node;

public class FormData {
	private DecisionTree decisionTree;
	private CMSForm form;
	private String employeeName;
	private int employeeId;
	private Integer rowId;

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

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	/**
	 * Saves filled form fields to database and decision tree to file.
	 */
	public void saveToDisk() {
		Logger.info("rowId inititally set to " + rowId);

		FilledFormFields filledFormFields = getFilledFormFields();
		String formName = form.getName();
		rowId = writeFormFieldsToDatabase(formName, employeeId, rowId,
				filledFormFields);

		Logger.info("rowId updated to " + rowId);

		String serializedDecisions = decisionTree.serialize();
		writeDecisionTreeToFile(formName, rowId, serializedDecisions);
	}

	/*
	 * Writes a file containing the entire decision tree. The filename is based
	 * on the form name and the row id. So if it's the Change Order form and row
	 * 2, the filename will be change_order2.decisions
	 */
	private static void writeDecisionTreeToFile(String formName, Integer rowId,
			String serializedDecisions) {
		String filename = formName + rowId + ".decisions";
		File decisionFile = new File(CMSGuidedFormFill.getDecisionsFile(),
				filename);
		try (PrintWriter decisionWriter = new PrintWriter(decisionFile)) {
			decisionWriter.write(serializedDecisions);
		} catch (IOException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	/*
	 * Creates a new row in the database and returns the row id number.
	 */
	private static Integer writeFormFieldsToDatabase(String formName,
			Integer employeeId, Integer rowId, FilledFormFields filledFormFields) {
		String sql = generateInsertOrUpdateStmt(formName, employeeId, rowId,
				filledFormFields);
		try (Connection c = DB.getConnection();
				PreparedStatement p = c.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);) {
			int i = 1;
			for (FilledFormField filledFormField : filledFormFields) {
				p.setString(i++, filledFormField.value);
			}
			p.execute();
			ResultSet generatedKeys = p.getGeneratedKeys();
			if (generatedKeys.first()) {
				return generatedKeys.getInt("GENERATED_KEY");
			} else if (rowId == null) {
				throw new RuntimeException("No generated keys returned on "
						+ "INSERT.");
			}
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		return rowId;
	}

	/*
	 * Generates an SQL INSERT or UPDATE statement with the formName as the
	 * table name and the names of the filled fields as the names of columns.
	 */
	private static String generateInsertOrUpdateStmt(String formName,
			Integer employeeId, Integer rowId, FilledFormFields filledFormFields) {
		StringBuilder columns = new StringBuilder();
		for (FilledFormField filledFormField : filledFormFields) {
			columns.append("\r\n    `").append(filledFormField.name)
					.append("`=?,");
		}
		columns.append("\r\n    `employee_id`=" + employeeId);

		String sql;
		if (rowId == null) {
			sql = "INSERT INTO " + formName + "\r\nSET" + columns;
		} else {
			sql = "UPDATE " + formName + "\r\nSET" + columns
					+ "\r\nWHERE `id`=" + rowId;
		}
		return sql + ";";
	}
}
