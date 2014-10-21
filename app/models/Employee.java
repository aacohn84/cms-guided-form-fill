package models;

import play.data.validation.Constraints.Required;

public class Employee {
	
	@Required
	private String username;
	
	@Required
	private String password;
	
	private String permissionLevel;
	private int employeeId;
	
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

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int id) {
		this.employeeId = id;
	}

}
