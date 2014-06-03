package de.so.ma.querynodes;

import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Log;

import de.so.ma.exceptions.ExpectedTypeHintException;
import de.so.ma.validation.matching.DMLMatcher;

public abstract class JCSqlDMLQuery extends JCSqlQuery {
	protected JCSqlDMLQuery(String queryString, JCExpression expectedTypeHint, Log log) {
		super(queryString, expectedTypeHint, log);
	}

	@Override
	protected void performQueryCallMethodSpecifics(Env<AttrContext> env, Attr attr) throws ExpectedTypeHintException {
		DMLMatcher dmlMatcher = getDMLMatcher();
		dmlMatcher.createColumnGetterMethodsMap();
	}

	@Override
	public Type getExpectedType() {
		return attr.getSyms().intType;
	}
	
	protected abstract DMLMatcher getDMLMatcher();
}
