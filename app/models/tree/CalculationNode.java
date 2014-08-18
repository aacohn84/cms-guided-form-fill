package models.tree;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import models.data.Decision;
import models.data.FilledFormFields;
import models.forms.CMSForm;
import play.twirl.api.Html;

public class CalculationNode extends SingleTargetNode {

	static class StoredSelection implements Serializable {
		static class Field implements Serializable {
			private static final long serialVersionUID = -6883068056446837545L;
			String name;
			String value;
		}
		
		private static final long serialVersionUID = -4566260486738356572L;
		
		Field[] fields;
	}
	
	static class CalculatedField {
		String name;
		Expression expr;

		public CalculatedField(String name, Expression expr) {
			this.name = name;
			this.expr = expr;
		}

		String value(FilledFormFields filledFormFields) {
			return expr.value(filledFormFields).toString();
		}
	}

	static interface Expression {
		public BigDecimal value(FilledFormFields filledFormFields);
	}

	static interface Operator {
		public BigDecimal operate(BigDecimal left, BigDecimal right);
	}

	static class AdditionOperator implements Operator {
		@Override
		public BigDecimal operate(BigDecimal left, BigDecimal right) {
			return left.add(right);
		}
	}

	static class SubtractionOperator implements Operator {
		@Override
		public BigDecimal operate(BigDecimal left, BigDecimal right) {
			return left.subtract(right);
		}
	}

	static class MultiplicationOperator implements Operator {
		@Override
		public BigDecimal operate(BigDecimal left, BigDecimal right) {
			return left.multiply(right);
		}
	}

	public static class Operators {
		public static final Operator ADD = new AdditionOperator();
		public static final Operator SUBTRACT = new SubtractionOperator();
		public static final Operator MULTIPLY = new MultiplicationOperator();
	}

	public static class BinaryExpr implements Expression {
		Expression left, right;
		Operator operator;

		public BinaryExpr(Expression left, Expression right, Operator operator) {
			this.left = left;
			this.right = right;
			this.operator = operator;
		}

		@Override
		public BigDecimal value(FilledFormFields formFields) {
			BigDecimal leftVal = left.value(formFields);
			BigDecimal rightVal = right.value(formFields);
			return operator.operate(leftVal, rightVal);
		}
	}

	public static class NumExpr implements Expression {
		BigDecimal numVal;

		public NumExpr(BigDecimal numVal) {
			this.numVal = numVal;
		}

		@Override
		public BigDecimal value(
				@SuppressWarnings("unused") FilledFormFields formFields) {
			return numVal;
		}
	}

	public static class RefExpr implements Expression {
		String referencedField;

		public RefExpr(String referencedField) {
			this.referencedField = referencedField;
		}

		@Override
		public BigDecimal value(FilledFormFields formFields) {
			String fieldVal = formFields.getFieldValue(referencedField);
			return new BigDecimal(fieldVal);
		}
	}

	public List<CalculatedField> calculatedFields = new ArrayList<>();

	public CalculationNode(String id, String idNext) {
		super(id, idNext, "", true);
	}

	public CalculationNode addCalculatedField(String name, Expression expr) {
		calculatedFields.add(new CalculatedField(name, expr));
		return this;
	}

	@Override
	public void fillFormFields(
			@SuppressWarnings("unused") String serializedObj,
			FilledFormFields formFields) {
		for (CalculatedField field : calculatedFields) {
			String value = field.value(formFields);
			formFields.fillField(field.name, value);
		}
	}

	@Override
	public Html renderSelectionAsHtml(String serializedSelection) {
		return new Html(getNodeHtml(serializedSelection));
	}

	@Override
	public String serializeInput(Map<String, String> input) {
		// create a list of field names & values
		List<StoredSelection.Field> fields = new ArrayList<>();
		for (CalculatedField calculatedField : calculatedFields) {
			StoredSelection.Field field = new StoredSelection.Field();
			field.name = calculatedField.name;
			field.value = input.get(calculatedField.name);
			fields.add(field);
		}
		// convert list to plain array for storage
		StoredSelection ss = new StoredSelection();
		ss.fields = new StoredSelection.Field[fields.size()];
		ss.fields = fields.toArray(ss.fields);
		return serializeAsString(ss);
	}

	/**
	 * Stores calculated values in the <code>rawInput</code> field of the
	 * {@link Decision}.
	 */
	@Override
	public Decision createDecision(CMSForm form,
			Map<String, String> requestData,
			FilledFormFields filledFormFields) {

		// Copy input parameters to allow modification without side-effects
		Map<String, String> requestDataCopy = new HashMap<>(requestData);
		FilledFormFields intermediates = new FilledFormFields();
		filledFormFields.copyTo(intermediates);
		
		/*
		 * Calculate each field and add it to the intermediates list to be used
		 * as a dependency in further calculations
		 */
		for (CalculatedField calculatedField : calculatedFields) {
			String fieldValue = calculatedField.value(intermediates);
			intermediates.fillField(calculatedField.name, fieldValue);
			
			/*
			 * Place calculated value in requestData so it can be used in a
			 * subsequent call to serializeInput.
			 */
			requestDataCopy.put(calculatedField.name, fieldValue);
		}
		return super.createDecision(form, requestDataCopy, filledFormFields);
	}
	
	@Override
	protected String getNodeHtml(String rawInput) {
		String html = new String();
		if (StringUtils.isNotEmpty(rawInput)) {
			StoredSelection ss = (StoredSelection) recreateObject(rawInput);
			for (StoredSelection.Field field : ss.fields) {
				html += "<strong>" + field.name + ":</strong> " + field.value
						+ "<br>";
			}
		}
		return html;
	}
}