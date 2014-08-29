package models.tree.fields;

import java.io.Serializable;

/**
 * A data structure for storing the user's selection, intended to be
 * serialized and stored as a String.
 * 
 * @author Aaron Cohn
 */
class StoredSelection implements Serializable {
	static class Field implements Serializable {
		private static final long serialVersionUID = -4801158335261387855L;

		public String name;
		public String value;
	}

	private static final long serialVersionUID = 1081518772940436931L;

	StoredSelection.Field[] fields;
}