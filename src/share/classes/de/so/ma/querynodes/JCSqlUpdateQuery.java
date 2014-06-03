package de.so.ma.querynodes;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.sun.tools.javac.util.Log;

import de.so.ma.codegenerator.specifics.SpecificCodeGenerator;
import de.so.ma.codegenerator.specifics.SpecificUpdateCodeGenerator;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.metarepo.Column;
import de.so.ma.metarepo.Table;
import de.so.ma.parser.UpdateQueryParser;
import de.so.ma.validation.QueryValidator;
import de.so.ma.validation.UpdateQueryValidator;
import de.so.ma.validation.matching.DMLMatcher;
import de.so.ma.validation.matching.UpdateMatcher;

public class JCSqlUpdateQuery extends JCSqlDMLQuery {
	private Table updateTable;
	private Collection<Column> updateColumns;
	private boolean objectUpdate;
	
	protected JCSqlUpdateQuery(String queryString, JCExpression expectedTypeHint, Log log) {
		super(queryString, expectedTypeHint, log);
	}

	@Override
	protected QueryValidator getValidator() {
		return new UpdateQueryValidator(log, this);
	}

	@Override
	protected void parse() throws NoSuchTableException, AmbiguousCoalesceException {
		UpdateQueryParser parser = new UpdateQueryParser(queryString, log);
		parser.parse();
		updateColumns = parser.getUpdateColumns();
		updateTable = parser.getTable();
		objectUpdate = parser.isObjectUpdate();
	}

	@Override
	public SpecificCodeGenerator getSpecificCodeGenerator() {
		return new SpecificUpdateCodeGenerator(this);
	}

	public Table getUpdateTable() {
		return updateTable;
	}

	public Collection<Column> getUpdateColumns() {
		return updateColumns;
	}

	public Collection<Column> getIdColumns() {
		return updateTable.getIdColumns();
	}
	
	public Iterable<Column> getCombinedUpdateAndIdColumns() {
		return Iterables.concat(getIdColumns(), getUpdateColumns());
	}

	public boolean isObjectUpdate() {
		return objectUpdate;
	}
	
	@Override
	protected DMLMatcher getDMLMatcher() {
		return new UpdateMatcher(this);
	}

	@Override
	public String getProcessedQueryString() {
		String processedQueryString = super.getProcessedQueryString();
		
		if (isObjectUpdate()) {
			processedQueryString = processedQueryString.replace("?", 
					getCombinedObjectReplacementPart());
		}
		
		return processedQueryString;
	}

	protected String getCombinedUpdatePartString(Collection<Column> relevantColumns) {
		Function<Column, String> columnStringElement = new Function<Column, String>() {
			@Override
			public String apply(Column setColumn) {
				return setColumn.getName() + "=?";
			}
		};
		
		Collection<String> setStringElements = Collections2.transform(relevantColumns, columnStringElement);
		
		return Joiner.on(",").join(setStringElements);
	}

	private CharSequence getCombinedObjectReplacementPart() {
		String combinedObjectReplacementPartTemplate =
				"%s WHERE %s";
		
		String combinedSetString = getCombinedUpdatePartString(getUpdateColumns());
		String combinedWhereString = getCombinedUpdatePartString(getIdColumns());
		
		return String.format(combinedObjectReplacementPartTemplate, 
				combinedSetString,
				combinedWhereString);
	}
}
