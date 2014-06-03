package de.so.ma.codegenerator.specifics;

import java.util.List;

import com.sun.tools.javac.code.Type;

import de.so.ma.codegenerator.MethodDeclarationCodeGenerator;
import de.so.ma.data.Parameter;
import de.so.ma.querynodes.JCSqlQuery;
import de.so.ma.types.TypesHelper;

public abstract class SpecificCodeGenerator {
	protected JCSqlQuery query;
	protected TypesHelper typesHelper;
	
	public SpecificCodeGenerator(JCSqlQuery query) {
		this.query = query;
		this.typesHelper = TypesHelper.getInstance();
	}
	
	public String getSetParameters() {
		StringBuilder codeBuilder = new StringBuilder();
		
		List<Parameter> params = query.getParameters();
		
		int paramIndex = 0;
		for (Parameter param : params) {
			Type paramType = param.getType();
			
			String setTemplate = 
					"ps.set%s(%s,param%s);\n";
			
			codeBuilder.append(
					String.format(setTemplate,
							getJdbcTypeName(paramType),
							paramIndex + 1,
							paramIndex)
					);
			
			paramIndex++;
		}
		
		return codeBuilder.toString();
	}

	public abstract String getMethodFooterTemplate(
			MethodDeclarationCodeGenerator methodDeclarationCodeGenerator);

	
	
	protected String wrapAsListTypeConversion(String typeConversion, Type expectedType) {
		String convTemplate = "" +
				"%s result = new %s();\n" + 
				"           while (rs.next()) {\n" + 
				"           	%s" + 
				"           	\n" + 
				"           	result.add(intermediateResult);\n" + 
				"           }";
		
		return String.format(convTemplate, 
				expectedType,
				expectedType.isInterface() ? 
						expectedType.toString().replace("List",  "ArrayList") : expectedType,
				typeConversion);
	}

	protected String getJdbcTypeName(Type type) {
		String typeString = type.isPrimitive() ? getPrimitiveTypeCounterpart(type)
				: type.toString();
	
		return TypesHelper.getInstance().getJdbcTypeName(typeString);
	}

	private String getPrimitiveTypeCounterpart(Type primitiveType) {
		return TypesHelper.getInstance().getBoxedType(primitiveType).toString();
	}
	
	
}
