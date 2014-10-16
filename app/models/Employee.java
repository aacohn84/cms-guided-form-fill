package models;

import play.data.validation.Constraints.Required;

public class Employee {
	
	@Required
	private String username;
	
	@Required
	private String password;
	
	private String permissionLevel;
	private int id;
	
	public String getEmployeeName() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPermissionLevel() {
		return permissionLevel;
	}
	
	public void setPermissionLevel(String permissionLevel) {
		this.permissionLevel = permissionLevel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
