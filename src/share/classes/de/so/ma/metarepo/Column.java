package de.so.ma.metarepo;

public class Column {
	private String name;
	private String typeName;
	private boolean id = false;
	private boolean nullable;
	private boolean autoIncrement;
	private boolean unique;
	private transient Table parentTable;
	private transient Column refersTo;

	public Column(String name, String typeName, Table parentTable) {
		this(name, typeName, parentTable, false, false, false);
	}
	
	public Column(String name, String typeName, Table parentTable, boolean nullable, boolean autoIncrement, boolean unique) {
		this.name = name;
		this.typeName = typeName;
		this.parentTable = parentTable;
		this.nullable = nullable;
		this.autoIncrement = autoIncrement;
		this.unique = unique;
	}
	
	public Table getParentTable() {
		return parentTable;
	}

	public String getName() {
		return name;
	}

	public String getTypeName() {
		return typeName;
	}

	public boolean isId() {
		return id;
	}
	
	public void setId(boolean id) {
		this.id = id;
	}

	public Column getRefersTo() {
		return refersTo;
	}

	public void setRefersTo(Column refersTo) {
		this.refersTo = refersTo;
	}

	public boolean isNullable() {
		return nullable;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public boolean isUnique() {
		return unique;
	}
}
