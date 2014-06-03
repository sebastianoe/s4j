package de.so.ma.parser.projectors;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import de.so.ma.parser.QueryTypeExtractor;

public class DoubleProjector extends LiteralProjector {

	public DoubleProjector(Expression expression, String alias, QueryTypeExtractor projectionsExtractor) {
		super(expression, alias, projectionsExtractor);
	}

	@Override
	protected String getTypeName() {
		return "java.lang.Double";
	}

	@Override
	protected String getNameString() {
		return ((DoubleValue) expression).toString();
	}

}
