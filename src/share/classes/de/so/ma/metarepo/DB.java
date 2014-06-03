package de.so.ma.metarepo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import com.google.common.collect.Lists;

public class DB {
	private LiveConnection liveConnection;
	private String name;
	private List<Table> tables;

	public LiveConnection getLiveConnection() {
		return liveConnection;
	}

	public String getName() {
		return name;
	}

	public List<Table> getTables() {
		return tables;
	}
	
	public Table getTable(String name) {
		for (Table table : tables) {
			if (table.getName().equals(name)) {
				return table;
			}
		}
		
		return null;
	}

	public void initFromLiveConnection() throws SQLException {
		Connection conn = liveConnection.getDBConnection();
		
		DatabaseMetaData metaData = conn.getMetaData();
		
		// use meta data to fill object structure
		name = conn.getCatalog();
		tables = getTablesFromMetaData(metaData); 
		
		setPrimaryKeys(metaData);
		setForeignKeyReferences(metaData);
	}

	private List<Table> getTablesFromMetaData(DatabaseMetaData metaData) throws SQLException {
		List<Table> tables = Lists.newArrayList();
		
		ResultSet rs = metaData.getTables(name, null, null, null);

		while (rs.next()) {
			String tableName = rs.getString("TABLE_NAME");
			Table table = new Table(tableName);
			table.setColumns(getColumnsFromMetaData(metaData, table));
			tables.add(table);
		}
		return tables;
	}

	private List<Column> getColumnsFromMetaData(DatabaseMetaData metaData, Table table) 
			throws SQLException {
		List<Column> columns = Lists.newArrayList();
	
		List<String> uniqueColumnNames = getUniqueColumnNames(metaData, table);
		
		// tbd: don't do this, use existing metadata instead!!!
		PreparedStatement ps = metaData.getConnection().prepareStatement("SELECT * FROM " + table.getName() + " LIMIT 0");
		ResultSet rs = ps.executeQuery();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			String columnName = rsmd.getColumnName(i);
			String columnClassName = rsmd.getColumnClassName(i);
			boolean isNullable = (rsmd.isNullable(i) == ResultSetMetaData.columnNullable);
			boolean isAutoIncrement = rsmd.isAutoIncrement(i);
			boolean isUnique = uniqueColumnNames.contains(columnName);
			
			Column column = new Column(
					columnName, columnClassName, table, 
					isNullable, isAutoIncrement, isUnique);
			columns.add(column);
		}
		
		return columns;
	}

	private void setPrimaryKeys(DatabaseMetaData metaData) throws SQLException {
		for (Table table : tables) {
			ResultSet rs = metaData.getPrimaryKeys(name, null, table.getName());
			while (rs.next()) {
				String pkColumnName = rs.getString("COLUMN_NAME");
				Column pkColumn = table.getColumnForName(pkColumnName);
				pkColumn.setId(true);
			}
		}	
	}

	private void setForeignKeyReferences(DatabaseMetaData metaData) throws SQLException {
		for (Table table : tables) {
			ResultSet rs = metaData.getImportedKeys(name, null, table.getName());
			while (rs.next()) {
				String fkColumnName = rs.getString("FKCOLUMN_NAME");
				String pkTableName = rs.getString("PKTABLE_NAME");
				String pkColumnName = rs.getString("PKCOLUMN_NAME");
				
				Column fkColumn = table.getColumnForName(fkColumnName);
				
				Table pkTable = getTable(pkTableName);
				Column pkColumn = pkTable.getColumnForName(pkColumnName);
				
				fkColumn.setRefersTo(pkColumn);
			}
		}
	}

	private List<String> getUniqueColumnNames(DatabaseMetaData metaData, Table table) throws SQLException {
		List<String> uniqueColumnNames = Lists.newArrayList();
		
		ResultSet indexRs = metaData.getIndexInfo(name, null, table.getName(), false, false);
		
		while (indexRs.next()) {
			uniqueColumnNames.add(indexRs.getString("COLUMN_NAME"));
		}
		
		return uniqueColumnNames;
	}
}
