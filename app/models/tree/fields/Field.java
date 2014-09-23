package models.tree.fields;

import play.twirl.api.Html;

public abstract class Field implements Cloneable {
	public boolean isRequired;
	public String label;
	public String name;
	public String value;

	public Field() {}
	
	public Field(String name, boolean isRequired, String label) {
		this.name = name;
		this.isRequired = isRequired;
		this.label = label;
		this.value = "";
	}
	
	public Field(String name, boolean isRequired, String label, String value) {
		this(name, isRequired, label);
		this.value = value;
	}
	
	@Override
	public final Field clone() {
		try {
			return (Field) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(); // Can't happen
		}
	}

	/**
	 * Returns an Html representation of the field.
	 */
	public abstract Html renderAsHtml();

	public final Field setLabel(String label) {
		this.label = label;
		return this;
	}

	public final Field setName(String name) {
		this.name = name;
		return this;
	}
	
	public final Field setRequired(boolean isRequired) {
		this.isRequired = isRequired;
		return this;
	}

	public final Field setValue(String value) {
		this.value = value;
		return this;
	}
}
