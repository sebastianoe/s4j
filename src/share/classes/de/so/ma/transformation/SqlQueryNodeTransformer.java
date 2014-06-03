package de.so.ma.transformation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.tree.JCTree.JCClassDecl;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;

import de.so.ma.codegenerator.reification.Reifier;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.ExpectedTypeHintException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.exceptions.ParameterValidationException;
import de.so.ma.querynodes.JCSqlQuery;

public class SqlQueryNodeTransformer {
	private static SqlQueryNodeTransformer instance = null;
	
	public static SqlQueryNodeTransformer getInstance() {
		if (instance == null) {
			instance = new SqlQueryNodeTransformer();
		}
		
		return instance;
	}
	
	private Multimap<JCClassDecl, String> declaratedQueries;
	private JCExpression nullExpression;
	
	/**
	 * Every potential sql query node has to pass this method.
	 * If the node is an sql query node, it will be transformed into an
	 * appropriate method definition (which will be placed as a static method 
	 * into the class) and the method call (which is used instead of the sql 
	 * query expression.
	 *  
	 * @param expression
	 * @param classDecl
	 * @param expectedType
	 * @param attr
	 * @param enter
	 * @param memberEnter
	 * @return
	 */
	public JCExpression transformPotentialSqlQuery(
			JCExpression expression, Env<AttrContext> env, Type expectedType, Attr attr, Enter enter, MemberEnter memberEnter) {
	
		if (expression instanceof JCSqlQuery) {
			JCSqlQuery sqlQuery = (JCSqlQuery) expression;
			
			JCClassDecl enclClass = env.enclClass;
			String queryIdentifier = sqlQuery.getIdentifier();
			
			// avoid to declare query methods more than once
			if (!declaratedQueries.get(enclClass).contains(queryIdentifier)) {
				try {
					sqlQuery.defineQueryCallMethod(env, attr, enter, memberEnter, expectedType);
					declaratedQueries.put(enclClass, queryIdentifier);
				} catch (ParameterValidationException e) {
					return nullExpression;
				} catch (ExpectedTypeHintException e) {
					return nullExpression;
				} catch (NoSuchTableException e) {
					return nullExpression;
				} catch (AmbiguousCoalesceException e) {
					attr.getLog().error("sql.parsing.ambiguous.coalesce", 
							e.getCoalesceExpression(), e.getType1(), e.getType2());
					return nullExpression;
				}
			}
			
			JCMethodInvocation queryCallMethodInvocation = sqlQuery.getQueryCallExpression(memberEnter);
			return queryCallMethodInvocation;
		} else { 
			return expression;
		}
	}

	private SqlQueryNodeTransformer() {
		declaratedQueries = ArrayListMultimap.create();
		nullExpression = Reifier.getInstance().reifyExpression("null");
	}
}
