package de.so.ma.exceptions;

public class NoSuchTableException extends SqlTransformationException {
	private static final long serialVersionUID = 3142304680857187493L;

	private final String tableName;
	
	public NoSuchTableException(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}
}
