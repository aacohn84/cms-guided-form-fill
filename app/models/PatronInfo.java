package models;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class PatronInfo {
	public static final PatronInfo EMPTY_PATRON_INFO = new PatronInfo(null, null, null, null, null);

	private static PatronInfo createPatronInfo(String name, String address,
			String phone, String email) {
		if (isEmpty(name) || isEmpty(address) || isEmpty(phone)) {
			throw new RuntimeException("Can't parse patron info because "
					+ "required fields are empty.");
		}
		return new PatronInfo(name, address, phone, email);
	}

	private static PatronInfo createPatronInfo(String name, String address,
			String phone, String email, Integer patronId) {
		return new PatronInfo(name, address, phone, email, patronId);
	}

	public static PatronInfo getPatron1Info(FilledFormFields filledFormFields) {
		String name = filledFormFields.getFieldValue("name_1");
		return getPatronInfo(filledFormFields, name);
	}

	public static PatronInfo getPatron2Info(FilledFormFields filledFormFields) {
		String name = filledFormFields.getFieldValue("name_2");
		return getPatronInfo(filledFormFields, name);
	}

	private static PatronInfo getPatronInfo(FilledFormFields filledFormFields,
			String name) {
		String address = filledFormFields.getFieldValue("address");
		String phone = filledFormFields.getFieldValue("phone");
		String email = filledFormFields.getFieldValue("email");
		return createPatronInfo(name, address, phone, email);
	}

	public static PatronInfo getPatronInfo(ResultSet rs) throws SQLException {
		String name = rs.getString("name");
		String address = rs.getString("address");
		String phone = rs.getString("phone");
		String email = rs.getString("email");
		Integer patronId = rs.getInt("patron_id");
		return createPatronInfo(name, address, phone, email, patronId);
	}

	private String address;
	private String email;
	private String name;
	private Integer patronId;
	private String phone;

	public PatronInfo() {} // only provided for JSON deserialization

	private PatronInfo(String name, String address, String phone, String email) {
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.email = email;
	}

	private PatronInfo(String name, String address, String phone,
			String email, Integer patronId) {
		this(name, address, phone, email);
		this.patronId = patronId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatronInfo other = (PatronInfo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}

	public String getAddress() {
		return address;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public Integer getPatronId() {
		return patronId;
	}

	public String getPhone() {
		return phone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPatronId(Integer patronId) {
		this.patronId = patronId;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "(" + name + ", " + address + ", " + phone + ", " + email + ")";
	}

	public String serialize() {
		return Json.toJson(this).toString();
	}

	public static PatronInfo deserialize(String serializedPatronInfo) {
		JsonNode jsonNode = Json.parse(serializedPatronInfo);
		return Json.fromJson(jsonNode, PatronInfo.class);
	}
}
