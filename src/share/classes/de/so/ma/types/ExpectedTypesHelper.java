package de.so.ma.types;

import static de.so.ma.util.Asserter.assertLen1List;

import java.util.List;

import com.google.common.collect.Lists;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.Filter;

public class ExpectedTypesHelper {
	/*
	 * Singleton methods
	 */
	private static ExpectedTypesHelper instance = null;
	
	public static ExpectedTypesHelper getInstance() {
		if (instance == null) {
			instance = new ExpectedTypesHelper();
		}
		
		return instance;
	}
	
	
	public List<MethodSymbol> getSettersOfType(Type type) {
		List<MethodSymbol> setters = Lists.newArrayList();
		
		Filter<Symbol> setterFilter = new Filter<Symbol>() {
			@Override
			public boolean accepts(Symbol t) {
				if (t instanceof MethodSymbol) {
					MethodSymbol ms = (MethodSymbol) t;
					// filter for methods that start with set and have exactly 1 parameter
					return ms.name.toString().startsWith("set") && ms.params.size() == 1;
				}
				return false;
			}
		};
		
		for (Symbol sym : type.tsym.members().getElements(setterFilter)) {
			setters.add((MethodSymbol) sym);
		}
		
		return setters;
	}
	
	public Type getParamTypeOfSetter(MethodSymbol methodSymbol) {
		List<VarSymbol> params = methodSymbol.params;
		assertLen1List(params);
		
		VarSymbol param = params.get(0);
		return param.type;
	}
}
