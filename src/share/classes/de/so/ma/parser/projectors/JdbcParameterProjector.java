package de.so.ma.parser.projectors;

import java.util.List;

import de.so.ma.data.Parameter;
import de.so.ma.data.ProjectionItem;
import de.so.ma.parser.QueryTypeExtractor;
import de.so.ma.types.TypesHelper;

public class JdbcParameterProjector extends Projector {
	public JdbcParameterProjector(QueryTypeExtractor projectionsExtractor) {
		super(null, projectionsExtractor);
	}

	@Override
	public ProjectionItem getProjection(List<ProjectionItem> fromProjections) {
		Parameter passedParameter = projectionsExtractor.getCurrentParameter();
		projectionsExtractor.incrementParameterCount();
		TypesHelper typesHelper = TypesHelper.getInstance();
		String typeName = typesHelper.getBoxedType(passedParameter.getType()).toString();
		
		ProjectionItem pi = 
				ProjectionItem.Builder.create(
						"JDBC Param", typeName).build();
		
		return pi;
	}
}
