package de.so.ma.codegenerator;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.so.ma.data.Parameter;
import de.so.ma.querynodes.JCSqlQuery;

public class MethodCallCodeGenerator {

	public String getQueryCall(JCSqlQuery query) {
		// tbd: refactor to use String templates
		String queryCall = 
				"SQL%s%s";
		
		return String.format(queryCall, 
				query.getIdentifier(), 
				getParametersCall(query));
	}

	private String getParametersCall(JCSqlQuery query) {
		StringBuilder pcBuilder = new StringBuilder();
		pcBuilder.append("(");
		
		List<String> paramsNames = Lists.newArrayList();
		for (Parameter param : query.getParameters()) {
			paramsNames.add(param.toString());
		}
		pcBuilder.append(Joiner.on(", ").join(paramsNames));
		
		pcBuilder.append(")");
		return pcBuilder.toString();
	}
	
}
