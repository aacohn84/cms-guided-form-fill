package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EmployeeHistoryEntry {
	public int formRowId;
	public Timestamp dateCreated;
	public Timestamp dateModified;
	public String patronName;

	public EmployeeHistoryEntry(ResultSet rs) throws SQLException {
		formRowId = rs.getInt("id");
		dateCreated = rs.getTimestamp("date_created");
		dateModified = rs.getTimestamp("date_modified");
		patronName = rs.getString("name_1");
	}
}
