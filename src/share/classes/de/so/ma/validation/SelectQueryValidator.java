package de.so.ma.validation;

import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.Iterables;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Log;

import de.so.ma.data.ProjectionItem;
import de.so.ma.querynodes.JCSqlQuery;
import de.so.ma.querynodes.JCSqlSelectQuery;
import de.so.ma.validation.matching.ProjectionMatcher;
import de.so.ma.validation.matching.flatobject.FlatObjectAttribute;

public class SelectQueryValidator extends QueryValidator {

	public SelectQueryValidator(Log log, JCSqlSelectQuery sqlQuery) {
		super(log, sqlQuery);
	}

	public void validateProjectionCompatibility(List<ProjectionItem> projections, Attr attr, Env<AttrContext> env, JCSqlQuery sqlQuery) {
		Type expectedType = sqlQuery.getExpectedType();
		
		if (typesHelper.isResultSet(expectedType)) {
			// the projections for expected ResultSets are not validated anymore
			return;
		}
		Type expectedRawType = typesHelper.getRawType(expectedType);
		
		if (typesHelper.isSimpleType(expectedRawType)) {
			validateTypeCompatibility(
					expectedRawType, Iterables.getOnlyElement(projections).getTypeName());
		} else if (typesHelper.isMapType(expectedType)) {
			validateMapCompatibility(expectedRawType, projections);
		} else {
			// expect object type (or list of that)
			BiMap<ProjectionItem, FlatObjectAttribute> projectionMapping = 
					validateObjectCompatibility(expectedRawType, projections);
			sqlQuery.setProjectionsMapping(projectionMapping);
		}
	}

	private void validateMapCompatibility(Type expectedRawType, List<ProjectionItem> projections) {
		if (projections.size() == 2) {
			ProjectionItem pi1 = projections.get(0);
			ProjectionItem pi2 = projections.get(1);
			
			if (pi1.isUnique()) {
				List<Type> paramTypes = expectedRawType.allparams();
				
				validateTypeCompatibility(paramTypes.get(0), pi1.getTypeName());
				validateTypeCompatibility(paramTypes.get(1), pi2.getTypeName());
			} else {
				log.error("sql.select.map.first.column.not.unique", pi1.getName());
			}
		} else {
			log.error("sql.select.map.not.two.projection");
		}
	}

	private void validateTypeCompatibility(Type expectedRawType, String queryTypeName) {
		if (!typesHelper.areTypesCompatible(expectedRawType, queryTypeName)) {
			log.error("sql.validation.types.not.compatible", queryTypeName, expectedRawType.toString());
		}
	}

	private BiMap<ProjectionItem, FlatObjectAttribute> validateObjectCompatibility(Type expectedType, List<ProjectionItem> projections) {
		ProjectionMatcher matcher = new ProjectionMatcher();
		return matcher.getMatching(expectedType, projections, sqlQuery, log);
	}

}
