package de.so.ma.codegenerator.specifics;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Iterables;
import com.sun.tools.javac.code.Type;

import de.so.ma.data.Parameter;
import de.so.ma.metarepo.Column;
import de.so.ma.querynodes.JCSqlQuery;
import de.so.ma.querynodes.JCSqlUpdateQuery;

public class SpecificUpdateCodeGenerator extends SpecificDMLCodeGenerator {
	public SpecificUpdateCodeGenerator(JCSqlQuery query) {
		super(query);
	}
	
	@Override
	public String getSetParameters() {
		JCSqlUpdateQuery updateQuery = (JCSqlUpdateQuery) query;

		if (updateQuery.isObjectUpdate()) {
			Parameter objectParam = Iterables.getOnlyElement(updateQuery.getParameters());
			Map<Column, Parameter> mappedParams = objectParam.getColumnGetterMethods();
			
			Collection<Column> updateColumns = updateQuery.getUpdateColumns();
			Collection<Column> idColumns = updateQuery.getIdColumns();

			StringBuilder codeBuilder = new StringBuilder();

			int paramIndex = 1;
			for (Column idColumn : Iterables.concat(updateColumns, idColumns)) {
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
