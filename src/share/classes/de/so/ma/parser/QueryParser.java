package de.so.ma.parser;

import java.io.StringReader;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import com.sun.tools.javac.util.Log;

import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.IncorrectParseTypeException;
import de.so.ma.exceptions.NoSuchTableException;

public abstract class QueryParser {
	protected String sqlQuery;
	protected CCJSqlParserManager parserManager;
	protected Log log;
	protected Statement statement;
	
	public QueryParser(String sqlQuery, Log log) {
		this.sqlQuery = sqlQuery;
		this.parserManager = new CCJSqlParserManager();
		this.log = log;
	}
	
	public void parse() throws NoSuchTableException, AmbiguousCoalesceException {
		try {
			statement = parserManager.parse(new StringReader(sqlQuery));
			validateQueryTypeCorrectness();
			parseTypeSpecific();
		} catch (JSQLParserException e) {
			// tbd: better exception handling
			throw new RuntimeException(e);
		} catch (NoSuchTableException e) {
			log.error("sql.parsing.no.such.table", e.getTableName());
			throw new NoSuchTableException(e.getTableName());
		} catch (IncorrectParseTypeException e) {
			// tbd: better exception handling
			throw new RuntimeException(e);
		}
	}

	protected abstract void validateQueryTypeCorrectness() throws IncorrectParseTypeException;
	protected abstract void parseTypeSpecific() throws NoSuchTableException, AmbiguousCoalesceException;
}
