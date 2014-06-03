package de.so.ma.parser;

import net.sf.jsqlparser.statement.select.Select;

import com.sun.tools.javac.util.Log;

import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.IncorrectParseTypeException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.querynodes.JCSqlSelectQuery;

public class SelectQueryParser extends QueryParser {
	private QueryTypeExtractor queryTypeExtractor;
	private JCSqlSelectQuery selectQuery;

	public SelectQueryParser(JCSqlSelectQuery jcSqlSelectQuery, Log log) {
		super(jcSqlSelectQuery.getQueryString(), log);
		this.selectQuery = jcSqlSelectQuery;
		this.queryTypeExtractor = null;
	}

	@Override
	protected void validateQueryTypeCorrectness() throws IncorrectParseTypeException {
		if (!(statement instanceof Select)) {
			throw new IncorrectParseTypeException();
		}
	}

	@Override
	protected void parseTypeSpecific() throws NoSuchTableException, AmbiguousCoalesceException {
		Select selectStatement = (Select) statement;
		
		queryTypeExtractor = new QueryTypeExtractor(selectStatement, selectQuery.getParameters());
	}
	
	public QueryTypeExtractor getQueryTypeExtractor() {
		return queryTypeExtractor;
	}
}
