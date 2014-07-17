package models.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import play.db.DB;

public class MySQLAuthDataStore implements AuthDataStore {

	@Override
	public boolean credentialsAreValid(String username, String password) {
		final String query = "{CALL credentialsAreValid(?,?)}";
		try (Connection c = DB.getConnection();
				CallableStatement cs = c.prepareCall(query);) {
			cs.setString(1, username);
			cs.setString(2, password);
			ResultSet rs = cs.executeQuery();
			if (rs.first()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String getPermissionLevel(String username) {
		final String query = "{CALL getPermissionLevel(?)}";
		try (Connection c = DB.getConnection();
				CallableStatement cs = c.prepareCall(query);) {
			cs.setString(1, username);
			ResultSet rs = cs.executeQuery();
			if (rs.first()) {
				return rs.getString("permission_level");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

}
