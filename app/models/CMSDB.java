package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;

import com.mysql.jdbc.Statement;

public class CMSDB {
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

	/**
	 * Checks for a patron with the same name/address in the database and
	 * returns the id if an entry is found.
	 */
	public static Integer getPatronId(PatronInfo patron) {
		String sql = "SELECT `patron_id` FROM `patron`"
				+ " WHERE `name`=? AND `address`=?;";
		try (Connection c = DB.getConnection();
				PreparedStatement pstmt = c.prepareStatement(sql);) {
			pstmt.setString(1, patron.getName());
			pstmt.setString(2, patron.getAddress());
			ResultSet rs = pstmt.executeQuery();
			if (rs.first()) {
				return rs.getInt("patron_id");
			}
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
	}

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

	/**
	 * Adds an association between the patron and a form entry to the
	 * patron_forms table (does nothing if the association already exists).
	 */
	public static void associatePatronWithFormEntry(Integer patronId,
			String formName, Integer rowId) {
		String sql = "INSERT INTO `patron_forms`"
				+ " SET `patron_id`=?, `form_name`=?, `form_row_id`=?"
				+ " ON DUPLICATE KEY UPDATE"
				+ " `patron_id`=VALUES(`patron_id`);";
		try (Connection c = DB.getConnection();
				PreparedStatement pstmt = c.prepareStatement(sql);) {
			pstmt.setInt(1, patronId);
			pstmt.setString(2, formName);
			pstmt.setInt(3, rowId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
	}
}
