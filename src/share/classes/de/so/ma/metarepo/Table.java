package de.so.ma.metarepo;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class Table {
	public Table(String name) {
		this.name = name;
	}

	private String name;
	private List<Column> columns;
	
	public Column getColumnForName(String columnName) {
		for (Column column : columns) {
			if (column.getName().equals(columnName)) {
				return column;
			}
		}
		
		return null;
	}

	public String getName() {
		return name;
	}
	
	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
	public Collection<Column> getIdColumns() {
		Predicate<Column> isIdPredicate = new Predicate<Column>() {
			@Override
			public boolean apply(Column column) {
				return column.isId();
			}
		};
		
		return Collections2.filter(getColumns(), isIdPredicate);
	}
}
