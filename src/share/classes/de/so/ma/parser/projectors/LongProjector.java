package de.so.ma.parser.projectors;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import de.so.ma.parser.QueryTypeExtractor;

public class LongProjector extends LiteralProjector {

	public LongProjector(Expression expression, String alias, QueryTypeExtractor projectionsExtractor) {
		super(expression, alias, projectionsExtractor);
	}

	@Override
	protected String getTypeName() {
		/*
		 *  as the parser can't differentiate between long and int,
		 *  we choose the one with the higher compatiblity to the
		 *  expected type
		 */
		return "java.lang.Integer";
	}

	@Override
	protected String getNameString() {
		return ((LongValue) expression).getStringValue();
	}
	
}
