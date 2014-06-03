package de.so.ma.codegenerator.specifics;

import de.so.ma.codegenerator.MethodDeclarationCodeGenerator;
import de.so.ma.querynodes.JCSqlQuery;

public abstract class SpecificDMLCodeGenerator extends SpecificCodeGenerator {
	public SpecificDMLCodeGenerator(JCSqlQuery query) {
		super(query);
	}
	
	@Override
	public String getMethodFooterTemplate(MethodDeclarationCodeGenerator methodDeclarationCodeGenerator) {
		return "			return ps.executeUpdate();\n";
	}
}
