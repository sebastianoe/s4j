package de.so.ma.parser;

import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.statement.delete.Delete;

import com.sun.tools.javac.util.Log;

import de.so.ma.exceptions.IncorrectParseTypeException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.metarepo.MetaRepo;
import de.so.ma.metarepo.Table;

public class DeleteQueryParser extends QueryParser {
	private Table table;
	private boolean objectDelete;
	
	public DeleteQueryParser(String sqlQuery, Log log) {
		super(sqlQuery, log);
	}

	@Override
	protected void validateQueryTypeCorrectness() throws IncorrectParseTypeException {
		if (!(statement instanceof Delete)) {
			throw new IncorrectParseTypeException();
		}
	}

	@Override
	protected void parseTypeSpecific() throws NoSuchTableException {
		Delete delete = (Delete) statement;
		MetaRepo repo = MetaRepo.getInstance();
		
		String tableName = delete.getTable().getName();
		table = repo.getDB().getTable(tableName);
		
		objectDelete = (delete.getWhere() instanceof JdbcParameter);
	}

	public Table getTable() {
		return table;
	}
	
	public boolean isObjectDelete() {
		return objectDelete;
	}
}
