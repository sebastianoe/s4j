package de.so.ma.parser.projectors;

import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.statement.select.SubSelect;
import de.so.ma.data.ProjectionItem;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.parser.QueryTypeExtractor;

/**
 * Base class for all projectors that are able to project some parsed projection expressions onto
 * valid ProjectionItems
 * @author Sebastian Oergel
 *
 */
public abstract class Projector {
	protected String alias;
	protected QueryTypeExtractor projectionsExtractor;
	
	public Projector(String alias, QueryTypeExtractor projectionsExtractor) {
		this.alias = alias;
		this.projectionsExtractor = projectionsExtractor;
	}
	
	public static Projector getInstance(Expression expression, String alias, QueryTypeExtractor queryTypeExtractor) {
		Projector instance = null;
		if (expression instanceof net.sf.jsqlparser.schema.Column) {
			instance = new ColumnProjector((net.sf.jsqlparser.schema.Column) expression, alias, queryTypeExtractor);
		} else if (expression instanceof Function) {
			instance = new FunctionProjector((Function) expression, alias, queryTypeExtractor);
		} else if (expression instanceof BinaryExpression) {
			instance = new BinaryExpressionProjector((BinaryExpression) expression, alias, queryTypeExtractor);
		} else if (expression instanceof LongValue) {
			instance = new LongProjector(expression, alias, queryTypeExtractor);
		} else if (expression instanceof StringValue) {
			instance = new StringProjector(expression, alias, queryTypeExtractor);
		} else if (expression instanceof DoubleValue) {
			instance = new DoubleProjector(expression, alias, queryTypeExtractor);
		} else if (expression instanceof Parenthesis) {
			instance = new ParenthesisProjector((Parenthesis) expression, queryTypeExtractor);
		} else if (expression instanceof JdbcParameter) {
			instance = new JdbcParameterProjector(queryTypeExtractor);
		} else if (expression instanceof SubSelect) {
			instance = new SubSelectProjector((SubSelect) expression, alias, queryTypeExtractor);
		} else {
			throw new RuntimeException("Not supported");
		}
		
		return instance;
	}
	
	public abstract ProjectionItem getProjection(List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException;
	
	protected String getTypeFromSubExpression(Expression expression, List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		Projector projector = Projector.getInstance(expression, null, projectionsExtractor);
		return projector.getProjection(fromProjections).getTypeName();
	}
}
