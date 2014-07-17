package models;

import play.data.validation.Constraints.Required;

public class User {
	
	@Required
	private String username;
	
	@Required
	private String password;
	
	private String permissionLevel;
	
	public String getUsername() {
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

}
