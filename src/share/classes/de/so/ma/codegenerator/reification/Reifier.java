package de.so.ma.codegenerator.reification;

import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.Parser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCCompilationUnit;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;

public class Reifier {
	private static Reifier instance = null;
	
	public static void initializeInstance(ParserFactory parserFactory) {
		if (instance == null) {
			instance = new Reifier();
			instance.parserFactory = parserFactory;
		}
	}
	
	public static void clearInstance() {
		instance = null;
	}
	
	public static Reifier getInstance() {
		return instance;
	}
	
	private ParserFactory parserFactory;
	
	public JCExpression reifyExpression(String expressionString) {
		JavacParser parser = getParserForString(expressionString);
		JCExpression parsedExpression = parser.parseExpression();
		
		return parsedExpression;
	}
	
	public JCMethodDecl reifyMethodDeclaration(String methodDeclaration) {
		String wrappedMethodDeclaration = wrapMethodDeclaration(methodDeclaration);
		
		JavacParser parser = getParserForString(wrappedMethodDeclaration);
		
		JCCompilationUnit compilationUnit = parser.parseCompilationUnit();
		JCTree classDefinition = compilationUnit.defs.head;
		if (classDefinition instanceof JCClassDecl) {
			JCClassDecl classDeclaration = (JCClassDecl) classDefinition;
			
			JCTree firstClassHeadDefinition = classDeclaration.defs.head;
			
			if (firstClassHeadDefinition instanceof JCMethodDecl) {
				return (JCMethodDecl) firstClassHeadDefinition;
			}
			// tbd: checked exception handling
			throw new RuntimeException("The first definition in the class declaration is not a method declaration");
		}
		// tbd: checked exception handling
		throw new RuntimeException("The first definition in the compilation unit is not a class declaration.");
	}
	
	private String wrapMethodDeclaration(String methodDeclaration) {
		StringBuilder sb = new StringBuilder();
		sb.append("class DummyClass { ");
		sb.append(methodDeclaration);
		sb.append("}");
		
		return sb.toString();
	}
	
	private JavacParser getParserForString(String stringToParse) {
		Parser parser = parserFactory.newParser(stringToParse, false, false, false);
		if (parser instanceof JavacParser) {
			return (JavacParser) parser;
		}
		// tbd: checked exception handling
		throw new RuntimeException("Created parser is not an instance of JavacParser");
	}

	public void setParserFactory(ParserFactory parserFactory) {
		this.parserFactory = parserFactory;
	}
}
