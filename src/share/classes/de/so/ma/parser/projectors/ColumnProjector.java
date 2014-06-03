package de.so.ma.parser.projectors;

import java.util.List;

import de.so.ma.data.ProjectionItem;
import de.so.ma.parser.QueryTypeExtractor;

public class ColumnProjector extends Projector{
	private net.sf.jsqlparser.schema.Column column;
	
	public ColumnProjector(net.sf.jsqlparser.schema.Column column, String alias, QueryTypeExtractor projectionsExtractor) {
		super(alias, projectionsExtractor);
		this.column = column;
	}

	@Override
	public ProjectionItem getProjection(List<ProjectionItem> fromProjections) {
		ProjectionItem columnProjectionItem = projectionsExtractor
				.getMatchingFromProjectionItemForColumn(column, fromProjections);
		
		if (alias != null) {
			columnProjectionItem.setName(alias);
		}
		
		return columnProjectionItem;
	}
}
