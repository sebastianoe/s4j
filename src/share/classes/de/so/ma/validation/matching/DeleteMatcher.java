package de.so.ma.validation.matching;

import java.util.Map;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Type;

import de.so.ma.data.Parameter;
import de.so.ma.metarepo.Column;
import de.so.ma.querynodes.JCSqlDeleteQuery;

public class DeleteMatcher implements DMLMatcher {
	private JCSqlDeleteQuery deleteQuery;

	public DeleteMatcher(JCSqlDeleteQuery deleteQuery) {
		this.deleteQuery = deleteQuery;
	}

	@Override
	public void createColumnGetterMethodsMap() {
		if (deleteQuery.isObjectDelete()) {
			Parameter param = Iterables.getOnlyElement(deleteQuery.getParameters());
			Map<Column, Parameter> idGetterMethods = Maps.newHashMap();
			
			for (Column idColumn : deleteQuery.getIdColumns()) {
				Parameter getterMethod = getIdGetterMethod(param.getObjectType(), idColumn);
				
				if (getterMethod != null) {
					idGetterMethods.put(idColumn, getterMethod);
				}
			}
			
			param.setColumnGetterMethods(idGetterMethods);
		}
	}
	
	private Parameter getIdGetterMethod(Type objectType, Column idColumn) {
		String columnName = idColumn.getName();
		Scope members = objectType.tsym.members();
		
		for (Symbol member : members.getElements()) {
			if (member instanceof MethodSymbol) {
				MethodSymbol methodMember = (MethodSymbol) member;
				if (methodMember.name.toString().equalsIgnoreCase("get" + columnName)
						&& methodMember.getParameters().size() == 0) {
					return new Parameter(
							methodMember.name.toString() + "()", 
							methodMember.type.getReturnType());
				}
			}
		}
		
		return null;
	}
}
