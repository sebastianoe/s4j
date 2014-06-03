package de.so.ma.parser.projectors;

import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import de.so.ma.data.ProjectionItem;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.parser.QueryTypeExtractor;

public class BinaryExpressionProjector extends Projector {
	private BinaryExpression expression;
	
	public BinaryExpressionProjector(BinaryExpression expression, String alias,
			QueryTypeExtractor projectionsExtractor) {
		super(alias, projectionsExtractor);
		this.expression = expression;
	}

	@Override
	public ProjectionItem getProjection(List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		String expressionResultType = getExpressionResultType(fromProjections);
		
		ProjectionItem expressionProjection = ProjectionItem.Builder.create(
				expression.getStringExpression(), expressionResultType).build();
		
		if (alias != null) {
			expressionProjection.setName(alias);
		}
		
		return expressionProjection;
	}

	private String getExpressionResultType(List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		String leftTypeName = getTypeFromSubExpression(expression.getLeftExpression(), fromProjections);
		String rightTypeName = getTypeFromSubExpression(expression.getRightExpression(), fromProjections);
		
		if (expression instanceof Multiplication || expression instanceof Addition || expression instanceof Subtraction) {
			return getIntegerPreservingType(leftTypeName, rightTypeName);
		} else if (expression instanceof Division) {
			return "java.lang.Double";
		}
		
		// tbd: better fault handling
		return null;
	}

	private String getIntegerPreservingType(String leftTypeName, String rightTypeName) {
		if (leftTypeName.equals("java.lang.Integer") && leftTypeName.equals(rightTypeName)) {
			return "java.lang.Integer";
		} else {
			return "java.lang.Double";
		}
	}
}
