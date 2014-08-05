package models.data;

public interface AuthStore {
	
	/**
	 * Returns true if credentials are valid.
	 */
	public boolean credentialsAreValid(String username, String password);
	
	/**
	 * Returns the permission level string of the requested user if the user
	 * exists. Otherwise returns empty string.
	 */
	public String getPermissionLevel(String username);
}
