package de.so.ma.validation.matching;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;

import de.so.ma.data.Parameter;
import de.so.ma.metarepo.Column;
import de.so.ma.querynodes.JCSqlInsertQuery;
import de.so.ma.types.TypesHelper;

public class InsertMatcher implements DMLMatcher {
	private JCSqlInsertQuery insertQuery;
	private TypesHelper typesHelper;

	public InsertMatcher(JCSqlInsertQuery insertQuery) {
		this.insertQuery = insertQuery;
		typesHelper = TypesHelper.getInstance();
	}

	@Override
	public void createColumnGetterMethodsMap() {
		if (insertQuery.isObjectInsert()) {
			Parameter param = Iterables.getOnlyElement(insertQuery.getParameters());
			Map<Column, Parameter> columnGetterMethods = Maps.newHashMap();

			for (Column column : insertQuery.getInsertColumns()) {
				List<String> resolvedColumnName = getResolvedColumnName(column);
				Parameter concatenatedMethod = 
						getConcatenatedMethod(param.getObjectType(), resolvedColumnName);
				
				if (concatenatedMethod != null) {
					columnGetterMethods.put(column, concatenatedMethod);
				}
			}
			
			param.setColumnGetterMethods(columnGetterMethods);
		}
	}
	
	private List<String> getResolvedColumnName(Column column) {
		List<String> resolvedColumnName = Lists.newArrayList();
		addToResolvedColumnName(column, resolvedColumnName);
		return resolvedColumnName;
	}
	
	private void addToResolvedColumnName(Column column, List<String> prefix) {
		if (column.getRefersTo() == null) {
			prefix.add(column.getName());
		} else {
			Column referringColumn = column.getRefersTo();
			prefix.add(referringColumn.getParentTable().getName());
			addToResolvedColumnName(referringColumn, prefix);
		}
	}
	
	private Parameter getConcatenatedMethod(Type type, List<String> resolvedColumnName) {
		String head = resolvedColumnName.get(0);
		Symbol getterMethod = getGetterMethod(head, type);
		
		if (getterMethod == null) {
			// no getter method for the resolved column name was found
			return null;
		}
		
		Type getterMethodReturnType = typesHelper.getBoxedType(getterMethod.type.getReturnType());
		
		if (typesHelper.isJdbcType(getterMethodReturnType)) {
			return new Parameter(getterMethod.name.toString() + "()", getterMethodReturnType);
		} else if (resolvedColumnName.size() >= 2) {
			Parameter subConcatenatedMethodName = 
					getConcatenatedMethod(getterMethodReturnType, resolvedColumnName.subList(1, resolvedColumnName.size()));
			
			if (subConcatenatedMethodName == null) {
				return null;
			} else { 
				return new Parameter(
						getterMethod.name.toString() 
							+ "()."
							+ subConcatenatedMethodName.getStringRepresentation(),
						subConcatenatedMethodName.getType());
			}
		}
		
		return null;
	}

	private Symbol getGetterMethod(String requiredName, Type objectType) {
		Scope members = objectType.tsym.members();
		
		for (Symbol member : members.getElements()) {
			if (member instanceof MethodSymbol) {
				MethodSymbol methodMember = (MethodSymbol) member;
	
				// try to identify setters: must start with set
				if (methodMember.name.toString().equalsIgnoreCase("get" + requiredName)
						&& methodMember.getParameters().size() == 0) {
					return methodMember;
				}
			}
		}
		
		return null;
	}
}
