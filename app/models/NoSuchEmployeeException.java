package models;

public class NoSuchEmployeeException extends RuntimeException {

	private static final long serialVersionUID = -924876106897727642L;

	public NoSuchEmployeeException(String username) {
		super("Employee [ " + username + " ] not found.");
	}
}
