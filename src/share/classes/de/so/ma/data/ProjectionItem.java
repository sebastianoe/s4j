package de.so.ma.data;

import com.google.common.base.Objects;

import de.so.ma.metarepo.Column;
import de.so.ma.metarepo.Table;

public class ProjectionItem {
	public static class Builder {
		public static Builder fromMetaRepoColumn(Column column) {
			ProjectionItem built = new ProjectionItem(column.getName(), column.getTypeName());

			return new Builder(built).baseTable(column.getParentTable().getName()).columnReference(column);
		}

		public static Builder create(String name, String typeName) {
			return new Builder(name, typeName);
		}

		private ProjectionItem built;

		private Builder(ProjectionItem built) {
			this.built = built;
		}

		private Builder(String name, String typeName) {
			built = new ProjectionItem(name, typeName);
		}

		public Builder baseTable(String baseTable) {
			built.setBaseTable(baseTable);
			return this;
		}

		public Builder aggregation(boolean aggregation) {
			built.setAggregation(aggregation);
			return this;
		}

		public Builder columnReference(Column column) {
			built.setColumn(column);
			return this;
		}

		public ProjectionItem build() {
			return built;
		}
		
		public Builder sameColumnReference() {
			built.setColumn(new Column(built.name, built.typeName, new Table(built.baseTable)));
			return this;
		}
	}

	protected ProjectionItem(String name, String typeName) {
		this.name = name;
		this.typeName = typeName;
	}

	private String name;
	private String typeName;

	// optional
	private String baseTable;
	private boolean aggregation = false;
	private Column column;
	private boolean groupedProjection = false;

	public String getOriginalTable() {
		Column originalColumn = getOriginalColumn();
		
		if (originalColumn == null) {
			return null;
		}
		
		return originalColumn.getParentTable().getName();
	}

	public String getOriginalName() {
		Column originalColumn = getOriginalColumn();
		
		if (originalColumn == null) {
			return null;
		}
		
		return originalColumn.getName();
	}
	
	public boolean isId() {
		if (getOriginalColumn() == null) {
			return false;
		} else {
			return getOriginalColumn().isId();
		}
	}
	
	public boolean isUnique() {
		if (getOriginalColumn() == null) {
			return false;
		} else {
			// tbd: complete grouped projection
			return getOriginalColumn().isUnique() || groupedProjection;
		}
	}
	
	private Column getOriginalColumn() {
		Column c = column;
		
		if (c == null) {
			return null;
		}
		
		while (c.getRefersTo() != null) {
			c = c.getRefersTo();
		}

		return c;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getBaseTable() {
		return baseTable;
	}

	public void setBaseTable(String baseTable) {
		this.baseTable = baseTable;
	}

	public boolean isAggregation() {
		return aggregation;
	}

	private void setAggregation(boolean aggregation) {
		this.aggregation = aggregation;
	}

	public boolean refersToFk() {
		return getColumn() != null && getColumn().getRefersTo() != null;
	}

	public Column getColumn() {
		return column;
	}

	public void setColumn(Column column) {
		this.column = column;
	}

	/**
	 * Generated code
	 */
	@Override
	public String toString() {
		return Objects
				.toStringHelper("PI")
				.addValue(name)
				.addValue(baseTable)
				.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(
				isAggregation(), 
				getOriginalName(), 
				getOriginalTable(), 
				getTypeName(), 
				getBaseTable(),
				getAggregationDeterminatorName());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		final ProjectionItem other = (ProjectionItem) obj;
		
		return 
				Objects.equal(isAggregation(), other.isAggregation()) &&
				Objects.equal(getOriginalName(), other.getOriginalName()) &&
				Objects.equal(getOriginalTable(), other.getOriginalTable()) &&
				Objects.equal(getTypeName(), other.getTypeName()) &&
				Objects.equal(getBaseTable(), other.getBaseTable()) &&
				Objects.equal(getAggregationDeterminatorName(), other.getAggregationDeterminatorName());
	}
	
	public void setGroupedProjection(boolean groupedProjection) {
		this.groupedProjection = groupedProjection;
	}
	
	public String getAggregationDeterminatorName() {
		if (isAggregation()) {
			return name;
		} else {
			return null;
		}
	}
}
