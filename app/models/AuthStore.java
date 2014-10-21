package models;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.Logger;
import play.db.DB;

public class AuthStore {
	private Employee employee;

	/**
	 * Returns <code>true</code> and loads the employee's data if the given
	 * credentials match an entry in the database.
	 */
	public boolean credentialsAreValid(String username, String password) {
		final String query = "{CALL credentialsAreValid(?,?)}";
		try (Connection c = DB.getConnection();
				CallableStatement cs = c.prepareCall(query);) {
			cs.setString(1, username);
			cs.setString(2, password);
			ResultSet rs = cs.executeQuery();
			if (rs.first()) {
				employee = new Employee();
				employee.setUsername(username);
				employee.setPassword(password);
				employee.setPermissionLevel(rs.getString("permission_level"));
				employee.setEmployeeId(rs.getInt("employee_id"));
				return true;
			}
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * Returns the employee's data if
	 * {@link AuthStore#credentialsAreValid(String, String)} previously returned
	 * <code>true</code>, otherwise returns <code>null</code>.
	 */
	public Employee getEmployee() {
		return employee;
	}
}
