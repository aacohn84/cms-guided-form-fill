package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.FilledFormFields.FilledFormField;
import play.Logger;
import play.db.DB;

import com.mysql.jdbc.Statement;

public class CMSDB {
	/**
	 * Create an entry in the database for this patron.
	 *
	 * @return the patron's database id number
	 */
	public static Integer createPatron(PatronInfo patron) {
		String sql = "INSERT INTO `patron`"
				+ " SET `name`=?, `address`=?, `phone`=?, `email`=?;";
		try (Connection c = DB.getConnection();
				PreparedStatement pstmt = c.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);) {
			pstmt.setString(1, patron.getName());
			pstmt.setString(2, patron.getAddress());
			pstmt.setString(3, patron.getPhone());
			pstmt.setString(4, patron.getEmail());
			pstmt.executeUpdate();
			ResultSet generatedKeys = pstmt.getGeneratedKeys();
			if (generatedKeys.first()) {
				return generatedKeys.getInt("GENERATED_KEY");
			}
			throw new RuntimeException("No generated keys returned on INSERT.");
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static void updatePatron(PatronInfo patron) {
		String sql = "UPDATE `patron`"
				+ "\r\nSET `name`=?, `address`=?, `phone`=?, `email`=?"
				+ "\r\nWHERE `patron_id`=?;";
		try (Connection c = DB.getConnection();
				PreparedStatement pstmt = c.prepareStatement(sql);) {
			pstmt.setString(1, patron.getName());
			pstmt.setString(2, patron.getAddress());
			pstmt.setString(3, patron.getPhone());
			pstmt.setString(4, patron.getEmail());
			pstmt.setInt(5, patron.getPatronId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	public static void deletePatron(Integer patronId) {
		String sql = "DELETE FROM `patron` WHERE `patron_id`=?";
		try (Connection c = DB.getConnection();
				PreparedStatement pstmt = c.prepareStatement(sql);) {
			pstmt.setInt(1, patronId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	public static List<EmployeeHistoryEntry> getEmployeeHistory(int employeeId,
			String formName) {
		String sql = "SELECT `id`, `date_created`, `date_modified`, `name_1`"
				+ "\r\nFROM `" + formName + "`"
				+ "\r\nWHERE `employee_id`=" + employeeId
				+ "\r\nORDER BY `date_created` desc;";
		List<EmployeeHistoryEntry> employeeHistory = new ArrayList<EmployeeHistoryEntry>();
		try (Connection c = DB.getConnection();
				PreparedStatement pstmt = c.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery();) {
			while (rs.next()) {
				employeeHistory.add(new EmployeeHistoryEntry(rs));
			}
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		return employeeHistory;
	}

	public static PatronInfo getPatron(Integer patronId) {
		String sql = "SELECT * FROM `patron` WHERE `patron_id`=?";
		try (Connection c = DB.getConnection();
				PreparedStatement pstmt = c.prepareStatement(sql);) {
			pstmt.setInt(1, patronId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				return PatronInfo.getPatronInfo(rs);
			}
			rs.close();
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static Integer newForm(String formName, int employeeId,
			int patron1Id, Integer patron2Id, FilledFormFields filledFormFields) {
		StringBuilder setColsClause = generateSetColumnsClause(filledFormFields);
		setColsClause.append(",\r\n    `employee_id`=" + employeeId)
					 .append(",\r\n    `patron_1_id`=" + patron1Id)
					 .append(",\r\n    `patron_2_id`=" + patron2Id);
		String sql = "INSERT INTO " + formName + setColsClause.toString() + ";";
		try (Connection c = DB.getConnection();
				PreparedStatement p = c.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);) {
			setStmtValues(filledFormFields, p);
			p.executeUpdate();
			ResultSet generatedKeys = p.getGeneratedKeys();
			if (generatedKeys.first()) {
				return generatedKeys.getInt("GENERATED_KEY");
			}
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		throw new RuntimeException("No generated key after insert.");
	}

	public static void updateForm(String formName, Integer rowId,
			Integer patron2Id, FilledFormFields filledFormFields) {
		StringBuilder setColsClause = generateSetColumnsClause(filledFormFields)
				.append(",\r\n    `patron_2_id`=" + patron2Id)
				.append(",\r\n    `date_modified`=CURRENT_TIMESTAMP()");
		String sql = "UPDATE " + formName + setColsClause.toString()
				+ "\r\nWHERE `id`=" + rowId + ";";
		try (Connection c = DB.getConnection();
				PreparedStatement p = c.prepareStatement(sql);) {
			setStmtValues(filledFormFields, p);
			p.executeUpdate();
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
	}

	// Generates the "SET" clause of the query for newForm and updateForm
	private static StringBuilder generateSetColumnsClause(
			FilledFormFields filledFormFields) {
		StringBuilder columns = new StringBuilder("\r\nSET");
		for (FilledFormField filledFormField : filledFormFields) {
			columns.append("\r\n    `").append(filledFormField.name)
					.append("`=?,");
		}
		// remove final comma
		columns.deleteCharAt(columns.length() - 1);

		return columns;
	}

	// Fills the values in the "SET" clause for newForm and updateForm
	private static void setStmtValues(FilledFormFields filledFormFields,
			PreparedStatement p) throws SQLException {
		int i = 1;
		for (FilledFormField filledFormField : filledFormFields) {
			p.setString(i++, filledFormField.value);
		}
	}
}
