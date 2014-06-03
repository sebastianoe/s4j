package de.so.ma.validation;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Log;

import de.so.ma.data.Parameter;
import de.so.ma.exceptions.ParameterValidationException;
import de.so.ma.metarepo.Column;
import de.so.ma.querynodes.JCSqlDeleteQuery;

public class DeleteQueryValidator extends QueryValidator {
	private JCSqlDeleteQuery deleteQuery;
	
	public DeleteQueryValidator(Log log, JCSqlDeleteQuery sqlQuery) {
		super(log, sqlQuery);
		this.deleteQuery = sqlQuery;
	}

	@Override
	public void validateParameterCompatibility(List<Parameter> params, Attr attr, Env<AttrContext> env)
			throws ParameterValidationException {
		if (deleteQuery.isObjectDelete()) {
			validateObjectIdsAvailable(Iterables.getOnlyElement(params));
		} else {
			super.validateParameterCompatibility(params, attr, env);
		}
	}

	private void validateObjectIdsAvailable(Parameter objectParam) throws ParameterValidationException {
		Set<Column> idGetterMethodKeys = objectParam.getColumnGetterMethods().keySet();
		
		Iterable<Column> idColumns = deleteQuery.getIdColumns();
		
		for (Column idColumn : idColumns) {
			if (!idGetterMethodKeys.contains(idColumn)) {
				log.error(
						"sql.delete.no.matching.id.column", 
						idColumn.getName(), 
						deleteQuery.getDeleteTable().getName());
				
				throw new ParameterValidationException();
			}
		}
	}
}
