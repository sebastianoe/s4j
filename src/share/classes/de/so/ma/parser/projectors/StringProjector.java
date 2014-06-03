package de.so.ma.parser.projectors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import de.so.ma.parser.QueryTypeExtractor;

public class StringProjector extends LiteralProjector {
	public StringProjector(Expression expression, String alias, QueryTypeExtractor projectionsExtractor) {
		super(expression, alias, projectionsExtractor);
	}

	@Override
	protected String getTypeName() {
		return "java.lang.String";
	}

	@Override
	protected String getNameString() {
		return ((StringValue) expression).getValue();
	}
	
	
}
