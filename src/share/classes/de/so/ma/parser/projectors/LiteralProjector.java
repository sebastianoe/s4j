package de.so.ma.parser.projectors;

import java.util.List;

import com.google.common.base.CharMatcher;

import net.sf.jsqlparser.expression.Expression;
import de.so.ma.data.ProjectionItem;
import de.so.ma.parser.QueryTypeExtractor;

public abstract class LiteralProjector extends Projector {
	protected Expression expression;
	
	public LiteralProjector(Expression expression, String alias, QueryTypeExtractor projectionsExtractor) {
		super(alias, projectionsExtractor);
		this.expression = expression;
	}
	
	@Override
	public ProjectionItem getProjection(List<ProjectionItem> fromProjections) {
			ProjectionItem pi = 
					ProjectionItem.Builder.create(
							stripQuotationCharacter(getNameString()), getTypeName()).build();
			
			if (alias != null) {
				pi.setName(stripQuotationCharacter(alias));
			}
			return pi;
	}
	
	protected String stripQuotationCharacter(String quotedString) {
		return CharMatcher.anyOf("`\"").trimFrom(quotedString);
	}
	
	protected abstract String getTypeName();
	protected abstract String getNameString();
	
}
