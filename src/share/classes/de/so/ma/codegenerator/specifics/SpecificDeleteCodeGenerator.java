package de.so.ma.codegenerator.specifics;

import java.util.Map;

import com.google.common.collect.Iterables;
import com.sun.tools.javac.code.Type;

import de.so.ma.data.Parameter;
import de.so.ma.metarepo.Column;
import de.so.ma.querynodes.JCSqlDeleteQuery;
import de.so.ma.querynodes.JCSqlQuery;

public class SpecificDeleteCodeGenerator extends SpecificDMLCodeGenerator {
	public SpecificDeleteCodeGenerator(JCSqlQuery query) {
		super(query);
	}

	@Override
	public String getSetParameters() {
		JCSqlDeleteQuery deleteQuery = (JCSqlDeleteQuery) query;

		if (deleteQuery.isObjectDelete()) {
			Parameter objectParam = Iterables.getOnlyElement(deleteQuery.getParameters());
			Map<Column, Parameter> mappedParams = objectParam.getColumnGetterMethods();
			Iterable<Column> idColumns = deleteQuery.getIdColumns();

			StringBuilder codeBuilder = new StringBuilder();

			int paramIndex = 1;
			for (Column idColumn : idColumns) {
				Parameter param = mappedParams.get(idColumn);
				param.safePrependStringRepresentationWith("param0.");

				Type paramType = param.getType();

				String setTemplate = "ps.set%s(%s,%s);\n";

				codeBuilder.append(String.format(setTemplate, getJdbcTypeName(paramType), paramIndex++, param));
			}

			return codeBuilder.toString();
		} else {
			return super.getSetParameters();
		}
	}
}
