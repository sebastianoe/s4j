package de.so.ma.parser;

import java.util.List;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.statement.insert.Insert;

import com.google.common.collect.Lists;
import com.sun.tools.javac.util.Log;

import de.so.ma.exceptions.IncorrectParseTypeException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.metarepo.Column;
import de.so.ma.metarepo.MetaRepo;
import de.so.ma.metarepo.Table;
import de.so.ma.util.SqlStringUtil;

public class InsertQueryParser extends QueryParser{
	private List<Column> columns;
	private Table table;
	private int valuesCount;
	
	public InsertQueryParser(String sqlQuery, Log log) {
		super(sqlQuery, log);
	}

	@Override
	protected void validateQueryTypeCorrectness() throws IncorrectParseTypeException {
		if (!(statement instanceof Insert)) {
			throw new IncorrectParseTypeException();
		}
	}

	@Override
	protected void parseTypeSpecific() throws NoSuchTableException {
		Insert insert = (Insert) statement;
		List<net.sf.jsqlparser.schema.Column> parsedColumns = insert.getColumns();
		
		String tableName = insert.getTable().getName();
		MetaRepo repo = MetaRepo.getInstance();
		table = repo.getDB().getTable(tableName);
		
		if (parsedColumns == null) {
			columns = table.getColumns();
		} else {
			columns = Lists.newArrayList();
			for (net.sf.jsqlparser.schema.Column parsedColumn : parsedColumns) {
				Column repoColumn = table.getColumnForName(SqlStringUtil.stripName(parsedColumn.getColumnName()));
				if (repoColumn != null) {
					columns.add(repoColumn);
				}
			}
		}
		
		ItemsList itemsList = insert.getItemsList();
		if (itemsList instanceof ExpressionList) {
			ExpressionList valuesList = (ExpressionList) itemsList;
			valuesCount = getValuesListCount(valuesList);
		} else if (itemsList instanceof MultiExpressionList) {
			MultiExpressionList multiValuesList = (MultiExpressionList) itemsList;
			
			valuesCount = 0;
			for (ExpressionList valuesList : multiValuesList.getExprList()) {
				valuesCount += getValuesListCount(valuesList);
			}
		}
	}
	
	private int getValuesListCount(ExpressionList valuesList) {
		return valuesList.getExpressions().size();
	}

	public List<Column> getColumns() {
		return columns;
	}

	public Table getTable() {
		return table;
	}

	public int getValueCount() {
		return valuesCount;
	}
}
