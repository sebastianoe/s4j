package de.so.ma.codegenerator.specifics;

import java.util.Map;

import com.google.common.collect.Iterables;
import com.sun.tools.javac.code.Type;

import de.so.ma.codegenerator.MethodDeclarationCodeGenerator;
import de.so.ma.data.Parameter;
import de.so.ma.metarepo.Column;
import de.so.ma.querynodes.JCSqlInsertQuery;
import de.so.ma.querynodes.JCSqlQuery;

public class SpecificInsertCodeGenerator extends SpecificDMLCodeGenerator {
	private JCSqlInsertQuery insertQuery;

	public SpecificInsertCodeGenerator(JCSqlQuery query) {
		super(query);
		insertQuery = (JCSqlInsertQuery) query;
	}

	@Override
	public String getSetParameters() {
		if (insertQuery.isObjectInsert()) {
			Parameter objectParam = Iterables.getOnlyElement(insertQuery.getParameters());
			Map<Column, Parameter> mappedParams = objectParam.getColumnGetterMethods();
			Iterable<Column> insertColumns = insertQuery.getInsertColumns();
			int paramIndex = 1;
			
			StringBuilder codeBuilder = new StringBuilder();

			if (objectParam.hasListType()) {
				codeBuilder.append("for (int elemCount = 0; elemCount < param0.size(); elemCount++) {\n");
				for (Column insertColumn : insertColumns) {
					Parameter param = mappedParams.get(insertColumn);
					param.safePrependStringRepresentationWith("param0.get(elemCount).");

					Type paramType = param.getType();

					String setTemplate = "ps.set%s(%s,%s);\n";

					codeBuilder.append(String.format(setTemplate, getJdbcTypeName(paramType), paramIndex++, param));
				}

				codeBuilder.append("	ps.addBatch();\n" + "}");
			} else {
				for (Column insertColumn : insertColumns) {
					Parameter param = mappedParams.get(insertColumn);
					param.safePrependStringRepresentationWith("param0.");

					Type paramType = param.getType();

					String setTemplate = "ps.set%s(%s,%s);\n";

					codeBuilder.append(String.format(setTemplate, getJdbcTypeName(paramType), paramIndex++, param));
				}
			}

			return codeBuilder.toString();
		} else {
			return super.getSetParameters();
		}
	}

	@Override
	public String getMethodFooterTemplate(MethodDeclarationCodeGenerator methodDeclarationCodeGenerator) {
		if (insertQuery.isObjectInsert() && Iterables.getOnlyElement(insertQuery.getParameters()).hasListType()) {
			return "			int sum = 0;\n" + "			for (int i : ps.executeBatch()) {\n" + " 				sum += i;\n" + "			}		"
					+ "			return sum;\n";
		} else {
			return "			return ps.executeUpdate();\n";
		}

	}
}
