package core.tree.fields;

import java.io.Serializable;

/**
 * A data structure for storing the user's selection, intended to be
 * serialized and stored as a String.
 * 
 * @author Aaron Cohn
 */
public class StoredSelection implements Serializable {
	private static final long serialVersionUID = 1081518772940436931L;

	public StoredField[] fields;
}