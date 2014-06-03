package de.so.ma.parser.projectors;

import static de.so.ma.util.Asserter.assertLen1List;

import java.util.List;

import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubSelect;
import de.so.ma.data.ProjectionItem;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.parser.QueryTypeExtractor;

public class SubSelectProjector extends Projector {
	private SubSelect expression;
	
	public SubSelectProjector(SubSelect subSelect, String alias,
			QueryTypeExtractor projectionsExtractor) {
		super(alias, projectionsExtractor);
		this.expression = subSelect;
	}

	@Override
	public ProjectionItem getProjection(List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		String expressionResultType = getExpressionResultType(fromProjections);
		
		ProjectionItem expressionProjection = ProjectionItem.Builder.create(
				alias, expressionResultType).build();
		
		if (alias != null) {
			expressionProjection.setName(alias);
		}
		
		return expressionProjection;
	}

	private String getExpressionResultType(List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		SelectBody selectBody = expression.getSelectBody();

		try {
			QueryTypeExtractor subQueryTypeExtractor = new QueryTypeExtractor(selectBody, null);
			List<ProjectionItem> subQueryProjections = subQueryTypeExtractor.getProjections();
			
			assertLen1List(subQueryProjections);
			ProjectionItem pi = subQueryProjections.get(0);
			return pi.getTypeName();
		} catch (NoSuchTableException e) {
			// tbd better handling for such unknown tables
			throw new RuntimeException(e);
		}
	}
}
