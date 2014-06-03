package de.so.ma.validation;

import java.util.List;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Log;

import de.so.ma.data.Parameter;
import de.so.ma.exceptions.MetaRepoNotSetException;
import de.so.ma.exceptions.ParameterValidationException;
import de.so.ma.metarepo.MetaRepo;
import de.so.ma.querynodes.JCSqlQuery;
import de.so.ma.types.TypesHelper;

public abstract class QueryValidator {
	protected Log log;
	protected TypesHelper typesHelper;
	protected JCSqlQuery sqlQuery;

	public QueryValidator(Log log, JCSqlQuery sqlQuery) {
		this.log = log;
		this.typesHelper = TypesHelper.getInstance();
		this.sqlQuery = sqlQuery;
	}

	/**
	 * Validate that the meta repository was set by the corresponding option.
	 * This method is only called in case of an SQL transformation. Thus, the
	 * repo option needs to be set.
	 * 
	 * @throws MetaRepoNotSetException
	 */
	public void validateRepoAvailability() throws MetaRepoNotSetException {
		if (MetaRepo.getInstance() == null) {
			log.error("sql.metarepo.not.set");
			throw new MetaRepoNotSetException();
		}
	}

	/**
	 * note: validation now only contains the creation of a typed node for each
	 * param, as MySQL seems to be quite generous with parameter types. Thus,
	 * the current strategy is to set the parameter depending on their type.
	 * Therefore, it only has to be contained in the set of JDBC types.
	 * 
	 * @param params
	 * @param attr
	 * @param env
	 * @throws ParameterValidationException
	 */
	public void validateParameterCompatibility(List<Parameter> params, Attr attr, Env<AttrContext> env)
			throws ParameterValidationException {
		for (Parameter parameter : params) {
			Type paramType = parameter.getType();
			if (!typesHelper.isJdbcType(typesHelper.getBoxedType(paramType))) {
				log.error("sql.validation.param.type.no.jdbctype", parameter.toString(),
						parameter.getNode().type.toString());

				throw new ParameterValidationException();
			}
		}
	}

	public Log getLog() {
		return log;
	}

}
