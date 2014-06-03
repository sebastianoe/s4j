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
import de.so.ma.querynodes.JCSqlQuery;
import de.so.ma.querynodes.JCSqlUpdateQuery;

public class UpdateQueryValidator extends QueryValidator {
	private JCSqlUpdateQuery updateQuery;
	
	public UpdateQueryValidator(Log log, JCSqlQuery sqlQuery) {
		super(log, sqlQuery);
		this.updateQuery = (JCSqlUpdateQuery) sqlQuery;
	}

	@Override
	public void validateParameterCompatibility(List<Parameter> params, Attr attr, Env<AttrContext> env) throws ParameterValidationException {
		if (updateQuery.isObjectUpdate()) {
			// tbd: validate parameters
			validateAllColumnsFillable();
		} else {
			super.validateParameterCompatibility(params, attr, env);
		}
	}
	
	/**
	 * Validate that all update and id columns have a corresponding getter method mapped
	 * @throws ParameterValidationException 
	 */
	private void validateAllColumnsFillable() throws ParameterValidationException {
		Parameter onlyParam = Iterables.getOnlyElement(updateQuery.getParameters());
		Set<Column> mapKeys = onlyParam.getColumnGetterMethods().keySet();
		
		for (Column updateOrIdColumn : updateQuery.getCombinedUpdateAndIdColumns()) {
			if (!mapKeys.contains(updateOrIdColumn)) {
				log.error("sql.update.no.getter.found", updateOrIdColumn.getName());
				throw new ParameterValidationException();
			}
		}
	}
}
