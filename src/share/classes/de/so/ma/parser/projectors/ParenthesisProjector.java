package de.so.ma.parser.projectors;

import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import de.so.ma.data.ProjectionItem;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.parser.QueryTypeExtractor;

public class ParenthesisProjector extends Projector {
	Parenthesis parenthesis;
	
	public ParenthesisProjector(Parenthesis parenthesis, QueryTypeExtractor projectionsExtractor) {
		super(null, projectionsExtractor);
		this.parenthesis = parenthesis;
	}

	@Override
	public ProjectionItem getProjection(List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		Expression parenthesisContent = parenthesis.getExpression();
		Projector parenthesisContentProjector = 
				Projector.getInstance(parenthesisContent, alias, projectionsExtractor);
		
		return parenthesisContentProjector.getProjection(fromProjections);
	}
}
