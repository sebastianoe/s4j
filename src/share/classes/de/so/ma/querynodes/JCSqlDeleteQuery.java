package de.so.ma.querynodes;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.util.Log;

import de.so.ma.codegenerator.specifics.SpecificCodeGenerator;
import de.so.ma.codegenerator.specifics.SpecificDeleteCodeGenerator;
import de.so.ma.data.Parameter;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.ExpectedTypeHintException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.metarepo.Column;
import de.so.ma.metarepo.Table;
import de.so.ma.parser.DeleteQueryParser;
import de.so.ma.validation.DeleteQueryValidator;
import de.so.ma.validation.QueryValidator;
import de.so.ma.validation.matching.DMLMatcher;
import de.so.ma.validation.matching.DeleteMatcher;

public class JCSqlDeleteQuery extends JCSqlDMLQuery {
	private Table deleteTable;
	private boolean objectDelete;
	
	protected JCSqlDeleteQuery(String queryString, JCExpression expectedTypeHint, Log log) {
		super(queryString, expectedTypeHint, log);
	}

	@Override
	protected QueryValidator getValidator() {
		return new DeleteQueryValidator(log, this);
	}

	@Override
	protected void performQueryCallMethodSpecifics(Env<AttrContext> env, Attr attr) throws ExpectedTypeHintException {
		DeleteMatcher matcher = new DeleteMatcher(this);
		matcher.createColumnGetterMethodsMap();
	}

	@Override
	protected void parse() throws NoSuchTableException, AmbiguousCoalesceException {
		DeleteQueryParser parser = new DeleteQueryParser(queryString, log);
		parser.parse();
		
		deleteTable = parser.getTable();
		objectDelete = parser.isObjectDelete();
	}

	@Override
	public SpecificCodeGenerator getSpecificCodeGenerator() {
		return new SpecificDeleteCodeGenerator(this);
	}

	@Override
	public Type getExpectedType() {
		return attr.getSyms().intType;
	}
	
	public boolean isObjectDelete() {
		return objectDelete;
	}

	public Table getDeleteTable() {
		return deleteTable;
	}
	
	@Override
	public String getProcessedQueryString() {
		String processedQueryString = super.getProcessedQueryString();
		
		if (isObjectDelete()) {
			processedQueryString = processedQueryString.replace("?", 
					Joiner.on(" AND ").join(getIdConditions()));
		}
		
		return processedQueryString;
	}
	
	public Iterable<String> getIdConditions() {
		Parameter objectParameter = Iterables.getOnlyElement(getParameters());
		
		Function<Column, String> conditionFunction = 
				new Function<Column, String>() {
			@Override
			public String apply(Column column) {
				return column.getName() + " = ?";
			}
		};
		
		return Iterables.transform(
				objectParameter.getColumnGetterMethods().keySet(), 
				conditionFunction);
	}
	
	public Iterable<Column> getIdColumns() {
		return deleteTable.getIdColumns();
	}

	@Override
	protected DMLMatcher getDMLMatcher() {
		return new DeleteMatcher(this);
	}
}
