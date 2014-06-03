package de.so.ma.validation;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Log;

import de.so.ma.data.Parameter;
import de.so.ma.exceptions.ParameterValidationException;
import de.so.ma.metarepo.Column;
import de.so.ma.querynodes.JCSqlInsertQuery;

public class InsertQueryValidator extends QueryValidator {
	private JCSqlInsertQuery insertQuery;

	public InsertQueryValidator(Log log, JCSqlInsertQuery sqlQuery) {
		super(log, sqlQuery);
		this.insertQuery = sqlQuery;
	}

	@Override
	public void validateParameterCompatibility(List<Parameter> params, Attr attr, Env<AttrContext> env)
			throws ParameterValidationException {
		List<Column> columns = insertQuery.getInsertColumns();

		if (insertQuery.getValuesCount() > 0 && insertQuery.getValuesCount() % columns.size() == 0) {
			// expect to have simple types
			super.validateParameterCompatibility(params, attr, env);
		} else if (insertQuery.isObjectInsert()) {
			validateAllColumnsFillable();
		} else {
			// wrong column count
			log.error("sql.insert.wrong.column.count", insertQuery.getValuesCount(), columns.size());
			throw new ParameterValidationException();
		}

		validateUpsertIdColumns();
		validateIllegalNullParameters(params);
	}

	private void validateUpsertIdColumns() throws ParameterValidationException {
		if (insertQuery.isUpsert()) {
			List<Column> upsertColumns = insertQuery.getInsertColumns();
			Collection<Column> idColumns = insertQuery.getInsertTable().getIdColumns();

			if (!upsertColumns.containsAll(idColumns)) {
				log.error("sql.upsert.id.columns.not.contained");
				throw new ParameterValidationException();
			}
		}
	}

	/**
	 * Validate that all insert columns have a corresponding getter method
	 * mapped
	 * 
	 * @throws ParameterValidationException
	 */
	private void validateAllColumnsFillable() throws ParameterValidationException {
		Parameter onlyParam = Iterables.getOnlyElement(insertQuery.getParameters());
		Set<Column> mapKeys = onlyParam.getColumnGetterMethods().keySet();

		for (Column insertColumn : insertQuery.getInsertColumns()) {
			if (!mapKeys.contains(insertColumn)) {
				log.error("sql.insert.no.getter.found", insertColumn.getName());
				throw new ParameterValidationException();
			}
		}
	}

	private void validateIllegalNullParameters(List<Parameter> params) {
		List<Column> missingColumns = Lists.newArrayList(insertQuery.getInsertTable().getColumns());
		List<Column> insertColumns = insertQuery.getInsertColumns();

		missingColumns.removeAll(insertColumns);

		for (Column missingColumn : missingColumns) {
			if (!missingColumn.isAutoIncrement() && !missingColumn.isNullable()) {
				log.error("sql.insert.illegal.null.column", missingColumn.getName());
			}
		}
	}
}
