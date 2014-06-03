package de.so.ma.querynodes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.sun.tools.javac.util.Log;

import de.so.ma.codegenerator.specifics.SpecificCodeGenerator;
import de.so.ma.codegenerator.specifics.SpecificInsertCodeGenerator;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.metarepo.Column;
import de.so.ma.metarepo.Table;
import de.so.ma.parser.InsertQueryParser;
import de.so.ma.types.TypesHelper;
import de.so.ma.validation.InsertQueryValidator;
import de.so.ma.validation.QueryValidator;
import de.so.ma.validation.matching.DMLMatcher;
import de.so.ma.validation.matching.InsertMatcher;

public class JCSqlInsertQuery extends JCSqlDMLQuery {
	private List<Column> insertColumns;
	private Table insertTable;
	private boolean upsert;
	private int valuesCount;
	
	private TypesHelper typesHelper;
	
	protected JCSqlInsertQuery(String queryString, JCExpression expectedTypeHint, Log log, boolean upsert) {
		super(queryString, expectedTypeHint, log);
		this.upsert = upsert;
		
		typesHelper = TypesHelper.getInstance();
	}

	@Override
	protected void parse() throws NoSuchTableException, AmbiguousCoalesceException {
		InsertQueryParser parser = new InsertQueryParser(queryString, log);
		parser.parse();
		insertColumns = parser.getColumns();
		insertTable = parser.getTable();
		valuesCount = parser.getValueCount();
	}

	@Override
	protected QueryValidator getValidator() {
		return new InsertQueryValidator(log, this);
	}

	public List<Column> getInsertColumns() {
		return insertColumns;
	}

	public Table getInsertTable() {
		return insertTable;
	}

	public boolean isObjectInsert() {
		return 
				getParameters().size() == 1 
				&& getInsertColumns().size() >= 1
				&& !typesHelper.isJdbcType(Iterables.getOnlyElement(getParameters()).getType());
	}

	@Override
	public String getProcessedQueryString() {
		String processedQueryString = super.getProcessedQueryString();
		
		if (isObjectInsert()) {
			processedQueryString = processedQueryString.replace("?", 
					getObjectQuestionsMarks());
		}
		
		if (isUpsert()) {
			processedQueryString = addUpsertAppendix(processedQueryString);
		}
		
		return processedQueryString;
	}

	private String addUpsertAppendix(String processedQueryString) {
		String appendixTemplate = 
				"%s ON DUPLICATE KEY UPDATE %s";
		
		return String.format(appendixTemplate,
				processedQueryString,
				getUpsertPartString());
	}

	public Collection<Column> getNonIdColumns() {
		Predicate<Column> noIdColumn = new Predicate<Column>() {
			@Override
			public boolean apply(Column column) {
				return !column.isId();
			}
		};
		
		return Collections2.filter(insertTable.getColumns(), noIdColumn);
	}

	private CharSequence getObjectQuestionsMarks() {
		List<String> questionMarksList = Collections.nCopies(
				insertColumns.size(), "?");
		return "(" + Joiner.on(",").join(questionMarksList) + ")";
	}

	@Override
	public SpecificCodeGenerator getSpecificCodeGenerator() {
		return new SpecificInsertCodeGenerator(this);
	}

	@Override
	protected DMLMatcher getDMLMatcher() {
		return new InsertMatcher(this);
	}
	
	public boolean isUpsert() {
		return upsert;
	}
	
	private String getUpsertPartString() {
		Function<Column, String> columnStringElement = new Function<Column, String>() {
			@Override
			public String apply(Column setColumn) {
				return setColumn.getName() + "=VALUES(" + setColumn.getName() + ")";
			}
		};
		
		Collection<String> setStringElements = Collections2.transform(getNonIdColumns(), columnStringElement);
		
		return Joiner.on(",").join(setStringElements);
	}

	public int getValuesCount() {
		return valuesCount;
	}
}