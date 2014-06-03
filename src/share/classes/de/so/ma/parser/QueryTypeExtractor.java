package de.so.ma.parser;

import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubSelect;

import com.google.common.collect.Lists;

import de.so.ma.data.Parameter;
import de.so.ma.data.ProjectionItem;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.exceptions.NoSuchTableException;
import de.so.ma.metarepo.Column;
import de.so.ma.metarepo.MetaRepo;
import de.so.ma.parser.projectors.Projector;
import de.so.ma.util.SqlStringUtil;

/**
 * Based on a parsed SELECT statement, this class can extract the projections and the parameter types.
 * @author Sebastian Oergel
 *
 */
public class QueryTypeExtractor {
	private List<ProjectionItem> queryProjections;
	private List<Parameter> passedParameters;
	private int parameterCount;
	
	/**
	 * Public method to get the list of projection of a SELECT statement.
	 * @param selectStatement the already-parsed SELECT statement
	 * @return a list of projections
	 * @throws NoSuchTableException 
	 * @throws AmbiguousCoalesceException 
	 */
	public QueryTypeExtractor(SelectBody selectBody, List<Parameter> passedParameters) throws NoSuchTableException, AmbiguousCoalesceException {
		this.passedParameters = passedParameters;
		this.parameterCount = 0;
		
		if (selectBody instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) selectBody;
			queryProjections = getProjections(plainSelect);
			
			markGroupByProjections(plainSelect);
		}
	}
	
	public QueryTypeExtractor(Select selectStatement, List<Parameter> passedParameters) throws NoSuchTableException, AmbiguousCoalesceException {
		this(selectStatement.getSelectBody(), passedParameters);
	}

	public ProjectionItem getMatchingFromProjectionItemForColumn(net.sf.jsqlparser.schema.Column column,
			List<ProjectionItem> fromProjections) {
		for (ProjectionItem fromProjection : fromProjections) {
			if (fromProjection.getName().equals(SqlStringUtil.stripName(column.getColumnName()))) {
				// skip from projection if select item's base table is set and doesn't match the from projection's one
				String columnTableName = column.getTable().getName();
				if (columnTableName != null && !columnTableName.equals(fromProjection.getBaseTable())) {
					// the base tables' names don't match, so skip this from-item
					continue;
				}
	
				return fromProjection;
			}
		}
		
		return null;
	}
	
	private List<ProjectionItem> getProjections(PlainSelect plainSelect) throws NoSuchTableException, AmbiguousCoalesceException {
		// get the projections of the from items
		List<ProjectionItem> fromProjections = getFromProjectionItems(plainSelect.getFromItem(), plainSelect.getJoins());
		
		// check if the from-projections are subject to projection
		List<ProjectionItem> projections = createProjections(plainSelect, fromProjections);
		return projections;
	}

	private List<ProjectionItem> createProjections(PlainSelect plainSelect, List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		List<ProjectionItem> projections = Lists.newArrayList();

		List<SelectItem> selectItems = plainSelect.getSelectItems();

		for (SelectItem selectItem : selectItems) {
			if (selectItem instanceof AllColumns) {
				addAllFromProjections(fromProjections, projections);
			} else if (selectItem instanceof AllTableColumns) {
				AllTableColumns allTableColumns = (AllTableColumns) selectItem;
				Table table = allTableColumns.getTable();

				addAllFromProjectionsOfTable(fromProjections, projections, table);
			} else if (selectItem instanceof SelectExpressionItem) {
				SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;
				Expression expression = selectExpressionItem.getExpression();

				addExpressionProjection(fromProjections, projections, expression, selectExpressionItem.getAlias());
			}
		}
		
		return projections;
	}

	private void addExpressionProjection(List<ProjectionItem> fromProjections, List<ProjectionItem> projections,
			Expression expression, String alias) throws AmbiguousCoalesceException {
		Projector projector = Projector.getInstance(expression, alias, this);
		projections.add(projector.getProjection(fromProjections));
	}

	private void addAllFromProjectionsOfTable(List<ProjectionItem> fromProjections, List<ProjectionItem> projections,
			Table table) {
		String tableName = table.getName();

		for (ProjectionItem projectionItem : fromProjections) {
			if (projectionItem.getBaseTable().equals(tableName)) {
				projections.add(projectionItem);
			}
		}
	}

	private void addAllFromProjections(List<ProjectionItem> fromProjections, List<ProjectionItem> projections) {
		for (ProjectionItem projectionItem : fromProjections) {
			projections.add(projectionItem);
		}
	}

	private List<ProjectionItem> getFromProjectionItems(FromItem fromItem, List<Join> joins) throws NoSuchTableException, AmbiguousCoalesceException {
		List<ProjectionItem> fromProjections = Lists.newArrayList();
		
		List<FromItem> fromSources = Lists.newArrayList();
		fromSources.add(fromItem);
		
		// add the joins
		if (joins != null) {
			for (Join join : joins) {
				fromSources.add(join.getRightItem());
			}
		}
			
		for (FromItem fromSource : fromSources) {
			if (fromSource instanceof Table) {
				fromProjections.addAll(getColumnsFromTable((Table) fromSource));
			} else if (fromSource instanceof SubSelect) {
				fromProjections.addAll(getColumnsFromSubSelect((SubSelect) fromSource));
			} else if (fromSource == null) {
				// do nothing
			} else {
				throw new RuntimeException("Not supported");
			}
		}
		
		return fromProjections;
	}

	private List<ProjectionItem> getColumnsFromSubSelect(SubSelect subSelect) throws NoSuchTableException, AmbiguousCoalesceException {
		SelectBody subSelectBody = subSelect.getSelectBody();
		if (subSelectBody instanceof PlainSelect) {
			List<ProjectionItem> subSelectProjections = getProjections((PlainSelect) subSelectBody);
			
			if (subSelect.getAlias() != null) {
				String subSelectAlias = subSelect.getAlias();
				for (ProjectionItem subSelectProjection : subSelectProjections) {
					subSelectProjection.setBaseTable(subSelectAlias);
				}
			}
			
			return subSelectProjections;
		} else {
			throw new RuntimeException("Not supported");
		}
	}

	private List<ProjectionItem> getColumnsFromTable(Table table) throws NoSuchTableException {
		de.so.ma.metarepo.Table metaRepoTable = MetaRepo.getInstance().getDB().getTable(table.getName());
		
		if (metaRepoTable == null) {
			throw new NoSuchTableException(table.getName());
		}
		
		List<Column> tableColumns = metaRepoTable.getColumns();

		List<ProjectionItem> projections = Lists.newArrayList();

		for (Column tableColumn : tableColumns) {
			projections.add(ProjectionItem.Builder.fromMetaRepoColumn(tableColumn).build());
		}
		
		if (table.getAlias() != null) {
			String tableAlias = table.getAlias();
			for (ProjectionItem tableColumn : projections) {
				tableColumn.setBaseTable(tableAlias);
			}
		}

		return projections;
	}

	private void markGroupByProjections(PlainSelect plainSelect) {
		List<Expression> groupByColumnRefs = plainSelect.getGroupByColumnReferences();
		
		if (groupByColumnRefs != null) {
			for (ProjectionItem pi : queryProjections) {
				for (Expression groupByColumnRef : groupByColumnRefs) {
					if (pi.getName().equals(groupByColumnRef.toString())) {
						pi.setGroupedProjection(true);
					}
				}
			}
		}
	}
	
	public void incrementParameterCount() {
		this.parameterCount++;
	}
	
	public Parameter getCurrentParameter() {
		return passedParameters.get(parameterCount);
	}

	public List<ProjectionItem> getProjections() {
		return queryProjections;
	}
}
