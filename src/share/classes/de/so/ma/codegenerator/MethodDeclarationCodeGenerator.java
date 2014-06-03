package de.so.ma.codegenerator;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.so.ma.codegenerator.specifics.SpecificCodeGenerator;
import de.so.ma.data.Parameter;
import de.so.ma.querynodes.JCSqlQuery;

/**
 * Concrete code generator class for generating the code
 * of an SQL query call method declaration.
 * @author Sebastian Oergel
 *
 */
public class MethodDeclarationCodeGenerator {
	private JCSqlQuery query;
	private SpecificCodeGenerator specificCodeGenerator;
	
	/**
	 * Constructor
	 * @param query The SQL query for which the method declaration should be generated.
	 */
	public MethodDeclarationCodeGenerator(JCSqlQuery query) {
		this.query = query;
		this.specificCodeGenerator = query.getSpecificCodeGenerator();
	}
	
	/**
	 * Creates the query call method declaration which performs the actual SQL DB query.
	 * @param expectedType Depending on the expected type, the method declaration has to look differently.
	 * @return A method declaration as a string, depending on the provided parameter.
	 */
	public String getQueryCallMethodDefinition() {
		// escape double quotes in query string
		String methodTemplate = "" +
				"    private static %s SQL%s(%s){        \n" + 
				"        try {\n" + 
				"            java.sql.Connection connection = de.so.ma.SqlConnectionManager.getConnection(); \n" + 
				"            java.sql.PreparedStatement ps = connection.prepareStatement(\"%s\");\n" + 
				"            // params\n" + 
				"            %s\n" + 
				"\n" + 
				"			 // type-specific template\n" +
				"%s" +
				"\n" + 
				"        } catch (java.sql.SQLException e) {\n" + 
				"            // tbd better exception handling\n" + 
				"            throw new RuntimeException(e);\n" + 
				"        }\n" + 
				"      }";
		
		String result = String.format(methodTemplate, 
				query.getExpectedType(),
				query.getIdentifier(),
				getParametersDeclaration(),
				query.getProcessedQueryString(),
				specificCodeGenerator.getSetParameters(),
				specificCodeGenerator.getMethodFooterTemplate(this));
		
		return result;
	}
	
	private String getParametersDeclaration() {
		List<Parameter> params = query.getParameters();
		List<String> typeStrings = Lists.newArrayList();
		
		for (int index = 0; index < params.size(); index++) {
			Parameter param = params.get(index);
			typeStrings.add(buildParamDeclarationString(param, index));
		}
		
		return Joiner.on(", ").join(typeStrings);
	}

	private String buildParamDeclarationString(Parameter param, int index) {
		StringBuilder paramBuilder = new StringBuilder();
		paramBuilder.append(param.getType().toString());
		paramBuilder.append(" param");
		paramBuilder.append(index);
		
		return paramBuilder.toString();
	}
}
