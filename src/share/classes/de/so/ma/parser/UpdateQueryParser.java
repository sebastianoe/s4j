package de.so.ma.parser;

import java.util.Collection;
import java.util.List;

import net.sf.jsqlparser.statement.update.Update;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.sun.tools.javac.util.Log;

import de.so.ma.exceptions.IncorrectParseTypeException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.metarepo.Column;
import de.so.ma.metarepo.MetaRepo;
import de.so.ma.metarepo.Table;
import de.so.ma.util.SqlStringUtil;

public class UpdateQueryParser extends QueryParser {
	private Collection<Column> updateColumns;
	private Table table;
	private boolean objectUpdate;

	public UpdateQueryParser(String sqlQuery, Log log) {
		super(sqlQuery, log);
		objectUpdate = false;
	}

	@Override
	protected void validateQueryTypeCorrectness() throws IncorrectParseTypeException {
		if (!(statement instanceof Update)) {
			throw new IncorrectParseTypeException();
		}
	}

	@Override
	protected void parseTypeSpecific() throws NoSuchTableException {
		Update update = (Update) statement;
		List<net.sf.jsqlparser.schema.Column> parsedColumns = update.getColumns();

		String tableName = update.getTable().getName();
		MetaRepo repo = MetaRepo.getInstance();
		table = repo.getDB().getTable(tableName);

		Predicate<Column> noIdColumn = new Predicate<Column>() {
			@Override
			public boolean apply(Column column) {
				return !column.isId();
			}
		};
		
		if (update.isObjectUpdate()) {
			objectUpdate = true;
			updateColumns = Collections2.filter(table.getColumns(), noIdColumn);
		} else {
			updateColumns = Lists.newArrayList();
			for (net.sf.jsqlparser.schema.Column parsedColumn : parsedColumns) {
				Column repoColumn = table.getColumnForName(SqlStringUtil.stripName(parsedColumn.getColumnName()));
				if (repoColumn != null) {
					updateColumns.add(repoColumn);
				}
			}
		}
	}

	public Collection<Column> getUpdateColumns() {
		return updateColumns;
	}
	
	public Table getTable() {
		return table;
	}
	
	public boolean isObjectUpdate() {
		return objectUpdate;
	}
}
