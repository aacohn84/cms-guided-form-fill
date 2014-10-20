package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EmployeeHistoryEntry {
	public Timestamp dateCreated;
	public String patronName;

	public EmployeeHistoryEntry(ResultSet rs) throws SQLException {
		dateCreated = rs.getTimestamp("date_created");
		patronName = rs.getString("name_1");
	}
}
