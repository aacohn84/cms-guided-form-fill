package core.tree;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import models.Decision;
import models.FilledFormFields;

import org.apache.commons.lang3.StringUtils;

import play.twirl.api.Html;
import core.tree.fields.StoredField;

public class CalculationNode extends NonBranchingNode {
	public static class StoredSelection implements Serializable {
		private static final long serialVersionUID = -4566260486738356572L;

		public StoredField[] fields;
	}

	static class CalculatedField {
		String name;
		Expression expr;

		public CalculatedField(String name, Expression expr) {
			this.name = name;
			this.expr = expr;
		}

		String value(FilledFormFields filledFormFields) {
			return expr.value(filledFormFields).setScale(2, RoundingMode.HALF_UP)
					.toString();
		}
	}

	public static interface Expression {
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

	/**
	 * Join multiple subexpressions by a common operation.
	 * <p>
	 * <strong>Ex:</strong><br>
	 * e1 + e2 + ... + eN
	 * </p>
	 *
	 * @author Aaron Cohn
	 */
	public static class nAryExpr implements Expression {
		private List<Expression> subExpressions;
		private Operator operator;

		public nAryExpr(Operator operator) {
			this.operator = operator;
			this.subExpressions = new ArrayList<Expression>();
		}

		public nAryExpr addExpr(Expression expression) {
			subExpressions.add(expression);
			return this;
		}

		@Override
		public BigDecimal value(FilledFormFields filledFormFields) {
			if (subExpressions.isEmpty()) {
				return BigDecimal.ZERO;
			}
			// get initial value
			BigDecimal result = subExpressions.get(0).value(filledFormFields);

			// reduction (sum, product, difference) over all sub-expressions
			int numExpressions = subExpressions.size();
			for (int exprIndex = 1; exprIndex < numExpressions; exprIndex++) {
				Expression expr = subExpressions.get(exprIndex);
				result = operator.operate(result, expr.value(filledFormFields));
			}
			return result;
		}
	}

	public static interface Condition extends Predicate<FilledFormFields> {}

	/**
	 * Allows one expression to be evaluated over another in case a certain
	 * condition is met.
	 *
	 * @author Aaron Cohn
	 */
	public static class ConditionalExpr implements Expression {
		private Expression exprTrue;
		private Expression exprFalse;
		private Condition condition;

		/**
		 * Construct a condtional expression.
		 *
		 * @param exprTrue
		 *            - evaluated if condition is satisfied.
		 * @param exprFalse
		 *            - evaluated if condition is <i>not</i> satisfied.
		 * @param condition
		 *            - condition to be tested.
		 */
		public ConditionalExpr(Expression exprTrue, Expression exprFalse,
				Condition condition) {
			this.exprTrue = exprTrue;
			this.exprFalse = exprFalse;
			this.condition = condition;
		}

		@Override
		public BigDecimal value(FilledFormFields filledFormFields) {
			if (condition.test(filledFormFields)) {
				return exprTrue.value(filledFormFields);
			}
			return exprFalse.value(filledFormFields);
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
		super(id, idNext, "", true, false);
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
	public String serializeInput(Map<String, String> input) {
		// create a list of field names & values
		List<StoredField> fields = new ArrayList<>();
		for (CalculatedField calculatedField : calculatedFields) {
			StoredField field = new StoredField();
			field.name = calculatedField.name;
			field.value = input.get(calculatedField.name);
			fields.add(field);
		}
		// convert list to plain array for storage
		StoredSelection ss = new StoredSelection();
		ss.fields = new StoredField[fields.size()];
		ss.fields = fields.toArray(ss.fields);
		return serializeAsString(ss);
	}

	/**
	 * Stores calculated values in the <code>rawInput</code> field of the
	 * {@link Decision}.
	 */
	@Override
	public Decision createDecision(Map<String, String> requestData,
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
		return super.createDecision(requestDataCopy, filledFormFields);
	}

	@Override
	public Html renderAsHtml(String rawInput) {
		String html = new String();
		if (StringUtils.isNotEmpty(rawInput)) {
			StoredSelection ss = recreateObject(rawInput, StoredSelection.class);
			for (StoredField field : ss.fields) {
				html += "<strong>" + field.name + ":</strong> " + field.value
						+ "<br>";
			}
		}
		return new Html(html);
	}
}
