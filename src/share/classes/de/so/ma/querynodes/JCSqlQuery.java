package de.so.ma.querynodes;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTree;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.Scanner;
import com.sun.tools.javac.parser.Token;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.util.Log;

import de.so.ma.codegenerator.MethodCallCodeGenerator;
import de.so.ma.codegenerator.MethodDeclarationCodeGenerator;
import de.so.ma.codegenerator.reification.Reifier;
import de.so.ma.codegenerator.specifics.SpecificCodeGenerator;
import de.so.ma.data.Parameter;
import de.so.ma.data.ProjectionItem;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.ExpectedTypeHintException;
import de.so.ma.exceptions.MetaRepoNotSetException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.exceptions.ParameterValidationException;
import de.so.ma.util.SqlStringUtil;
import de.so.ma.util.StateHelper;
import de.so.ma.validation.QueryValidator;
import de.so.ma.validation.matching.flatobject.FlatObject;
import de.so.ma.validation.matching.flatobject.FlatObjectAttribute;

/**
 * This class saves all relevant information about an SQL query. It extends the
 * JCMethodInvocation parser node in order to come through from the parser up to
 * the attribution phase. There, it will be "transformed" into the actual method
 * call. This method will be generated and added to the parent class on the fly.
 * This allows to handle even more complex SQL queries as parser expressions
 * (e.g. to use them as parameters).
 * <p>
 * The method extends the JCMethodInvocation node class, as this is a concrete child
 * class of JCExpression that can be used as a template. 
 * 
 * @author Sebastian Oergel
 * 
 */
public abstract class JCSqlQuery extends JCMethodInvocation {
	/**
	 * Creates a JCSqlQuery object by reading the tokens from
	 * the provided scanner/lexer. It stops reading on the terminating
	 * character (right bracket).
	 * @param scanner The scanner to read the tokens.
	 * @param f 
	 * @return A new JCSqlQuery instance
	 */
	public static JCSqlQuery createFromTokens(Scanner scanner, JavacParser parser, boolean includesExpectedTypeHint) {
		Log log = scanner.getLog();
		
		int startPos = scanner.pos();
		
		JCExpression expectedTypeHint = null;
		if (includesExpectedTypeHint) {
			int prevMode = parser.getMode();
			parser.setMode(JavacParser.TYPE);
			expectedTypeHint = parser.term3();
			parser.setMode(prevMode);

			scanner.nextToken();
			if (scanner.token() != Token.LBRACKET) {
				throw new RuntimeException("An expected type hint must be directly followed by an SQL query in brackets");
			}
			
			scanner.nextToken();
			startPos = scanner.pos();
		}
		scanner.setRawMode(true);
		int endPos = readTokens(scanner, Token.RBRACKET, "unclosed sql query");
		String sqlString = String.valueOf(scanner.getRawCharacters(startPos, endPos));
		sqlString = SqlStringUtil.fixWhitespaces(sqlString);
		
		scanner.setRawMode(false);
		
		String loweredSqlString = sqlString.toLowerCase();
		
		JCSqlQuery sqlQuery = null;
		if (loweredSqlString.startsWith("select")) {
			sqlQuery = new JCSqlSelectQuery(sqlString, expectedTypeHint, log);
		} else if (loweredSqlString.startsWith("insert into")) {
			sqlQuery = new JCSqlInsertQuery(sqlString, expectedTypeHint, log, false);
		} else if (loweredSqlString.startsWith("delete from")) {
			sqlQuery = new JCSqlDeleteQuery(sqlString, expectedTypeHint, log);
		} else if (loweredSqlString.startsWith("update")) {
			sqlQuery = new JCSqlUpdateQuery(sqlString, expectedTypeHint, log);
		} else if (loweredSqlString.startsWith("upsert") && loweredSqlString.endsWith(" with primary key")) {
			sqlQuery = new JCSqlInsertQuery(transformUpsertQuery(sqlString), expectedTypeHint, log, true);
		} else {
			// tbd: better exception handling
			throw new RuntimeException("Query type is not supported");
		}

		return sqlQuery;
	}
	
	private static String transformUpsertQuery(String sqlString) {
		return sqlString
				.replaceFirst("(?i)upsert", "INSERT INTO")
				.replaceFirst("(?i) with primary key", "");
	}

	private static int readTokens(Scanner scanner, Token limitingToken, String unclosedError) {
		scanner.nextToken();
		while (scanner.token() != limitingToken) {
			if (scanner.token() == Token.SEMI) {
				throw new RuntimeException(unclosedError);
			}
			scanner.nextToken();
		}
		
		return scanner.pos();
	}

	protected String queryString;
	
	protected List<Parameter> passedParameters;
	private BiMap<ProjectionItem, FlatObjectAttribute> projectionsMapping;
	private FlatObject flatObject;
	protected Log log;
	protected QueryValidator validator;
	protected Type expectedType;
	protected Attr attr;
	
	// optional
	private JCExpression expectedTypeHint;
	

	protected JCSqlQuery(String queryString, JCExpression expectedTypeHint, Log log) {
		// nonsense parent constructor call, as this only creates a dummy
		super(null, null, null);
		this.log = log;
		this.queryString = queryString;
		this.expectedTypeHint = expectedTypeHint;
		this.validator = getValidator();
		this.projectionsMapping = null;
		
		
		extractParameters();

		try {
			validator.validateRepoAvailability();
		} catch (MetaRepoNotSetException e) {
			// for now, do nothing, just don't execute parse
			// tbd: make error handling more explicite
		}
	}
	
	protected abstract QueryValidator getValidator();

	/**
	 * Accept-method of the visitor pattern. This allows arbitrary concrete visitors
	 * to make use of a {@link JCSqlQuery}. Note that such visitors have to implement
	 * the visitSqlQuery(JCSqlQuery) method for this.
	 * @param v The visitor instance
	 */
	@Override
	public void accept(Visitor v) {
		v.visitSqlQuery(this);
	}

	/**
	 * This method defines a query call method for the sql query of subject.
	 * This method is placed in the enclosing class of the environment.
	 * The several parameters are required for different kinds of type checking
	 * and on-the-fly attribution of generated code.
	 * 
	 * @param env The method's environment. It's the environment in which the query node was placed.
	 * @param attr The calling Attr instance for attribution of live-generated code.
	 * @param enter The Enter instance for entering live-generated code.
	 * @param memberEnter The MemberEnter instance for entering live-generated code.
	 * @param expectedType The expected type to determine the query call method's structure.
	 * @throws ParameterValidationException
	 * @throws ExpectedTypeHintException 
	 * @throws NoSuchTableException 
	 * @throws AmbiguousCoalesceException 
	 */
	public void defineQueryCallMethod(Env<AttrContext> env, Attr attr, Enter enter, MemberEnter memberEnter,
			Type expectedType) throws ParameterValidationException, ExpectedTypeHintException, NoSuchTableException, AmbiguousCoalesceException {
		this.expectedType = expectedType;
		this.attr = attr;
		
		// Check that parameters are valid.
		// They already exist as nodes, but aren't typed, yet.
		attributeParameters(env);
		
		parse();
		
		performQueryCallMethodSpecifics(env, attr);
		validator.validateParameterCompatibility(passedParameters, attr, env);
		
		// create code generator
		MethodDeclarationCodeGenerator codeGenerator = new MethodDeclarationCodeGenerator(this);
		String methodDeclaration = codeGenerator.getQueryCallMethodDefinition();

		// create nested parse node from string
		JCMethodDecl queryCallMethodDeclaration = Reifier.getInstance().reifyMethodDeclaration(methodDeclaration);
		
		// append it to the current class
		JCClassDecl classDecl = env.enclClass;
		classDecl.defs = classDecl.defs.append(queryCallMethodDeclaration);

		// enter query call method declaration
		Env<AttrContext> enterEnv = enter.getTypeEnvs().get(classDecl.sym);
		memberEnter.setEnv(enterEnv);
		queryCallMethodDeclaration.accept(memberEnter);

		// attribute for type information
		queryCallMethodDeclaration.accept(attr);
	}

	private void attributeParameters(Env<AttrContext> env) {
		for (Parameter parameter : passedParameters) {
			parameter.createNode(attr, env);
		}
	}

	protected abstract void performQueryCallMethodSpecifics(Env<AttrContext> env, Attr attr) throws ExpectedTypeHintException;

	/**
	 * Returns a {@link JCMethodInvocation} node which calls the query call method defined
	 * by the defineQueryCallMethod method.
	 * <p>
	 * The returned node is intended to replace the {@link JCSqlQuery} node, as it is a normal
	 * method call that can be handled appropriately.
	 * @param memberEnter The current MemberEnter instance to enter live-generated code.
	 * @return A {@link JCMethodInvocation} node which is intended to replace the {@link JCSqlQuery} node. 
	 */
	public JCMethodInvocation getQueryCallExpression(MemberEnter memberEnter) {
		MethodCallCodeGenerator codeGenerator = new MethodCallCodeGenerator();
		String queryCall = codeGenerator.getQueryCall(this);

		// create expression parse node from string
		JCExpression queryCallExpression = Reifier.getInstance().reifyExpression(queryCall);

		if (queryCallExpression instanceof JCMethodInvocation) {
			// enter it to symbol table
			queryCallExpression.accept(memberEnter);

			// note: attribution will happen in the calling method in the Attr
			// class, as this will need the expected type
			return (JCMethodInvocation) queryCallExpression;
		}
		// tbd: checked exception handling
		throw new RuntimeException("Parsed query call expression is no method invocation");
	}

	/**
	 * Get the query's parameters
	 * 
	 * @return the query's parameters
	 */
	public List<Parameter> getParameters() {
		return passedParameters;
	}
	
	/**
	 * Get the raw query string
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * Get identifier string for internal query string. Rely on SHA-256 hash,
	 * here.
	 * 
	 * @return the identifier
	 */
	public String getIdentifier() {
		HashFunction hf = Hashing.sha256();
		return hf.hashString(queryString).toString();
	}
	
	public BiMap<ProjectionItem, FlatObjectAttribute> getProjectionsMapping() {
		return projectionsMapping;
	}

	public void setProjectionsMapping(BiMap<ProjectionItem, FlatObjectAttribute> projectionsMapping) {
		this.projectionsMapping = projectionsMapping;
	}

	public FlatObject getFlatObject() {
		return flatObject;
	}

	public void setFlatObject(FlatObject flatObject) {
		this.flatObject = flatObject;
	}
	
	public JCExpression getExpectedTypeHint() {
		return expectedTypeHint;
	}
	
	public abstract Type getExpectedType();
	
	/**
	 * The string representation of the SQL query node
	 */
	@Override
	public String toString() {
		return "SQL[" + queryString + "]";
	}
	
	private void extractParameters() {
		String replacedQueryString = queryString;
		passedParameters = Lists.newArrayList();

		Matcher matcher = Pattern.compile("(\\$.*?\\$)").matcher(queryString);
		while (matcher.find()) {
			String foundSubstring = matcher.group(1);
			
			// add parameter & remove escaping dollar sign
			passedParameters.add(new Parameter(foundSubstring.substring(1, foundSubstring.length() - 1)));
			replacedQueryString = replacedQueryString.replace(foundSubstring, "?");
		}

		queryString = replacedQueryString;
	}

	protected abstract void parse() throws NoSuchTableException, AmbiguousCoalesceException;

	public String getProcessedQueryString() {
		return queryString.replace("\"", "\\\"");
	}
	
	public abstract SpecificCodeGenerator getSpecificCodeGenerator();
}
