package models.data;

public class NoSuchUserException extends RuntimeException {

	private static final long serialVersionUID = -924876106897727642L;

	public NoSuchUserException(String username) {
		super("User [ " + username + " ] not found.");
	}
}
