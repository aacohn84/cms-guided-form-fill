package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import play.Logger;
import play.db.DB;

public class CMSDB {
	public static List<EmployeeHistoryEntry> getEmployeeHistory(int employeeId,
			String formName) {
		String sql =
				"SELECT date_created, name_1"
				+ "\r\nFROM " + formName
				+ "\r\nWHERE `employee_id`=" + employeeId
				+ "\r\nORDER BY date_created desc;";
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
}
