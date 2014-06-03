package de.so.ma.parser.projectors;

import static de.so.ma.util.Asserter.assertLen1List;
import static de.so.ma.util.Asserter.assertLenNParams;

import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

import com.google.common.collect.Lists;

import de.so.ma.data.ProjectionItem;
import de.so.ma.exceptions.AmbiguousCoalesceException;
import de.so.ma.parser.QueryTypeExtractor;

public class FunctionProjector extends Projector {
	private Function function;

	public FunctionProjector(Function function, String alias, QueryTypeExtractor projectionsExtractor) {
		super(alias, projectionsExtractor);
		this.function = function;
	}

	@Override
	public ProjectionItem getProjection(List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		String functionResultType = getFunctionResultType(fromProjections);
		
		ProjectionItem functionProjection = ProjectionItem.Builder.create(function.getName(), functionResultType).aggregation(true).build();
		if (alias != null) {
			functionProjection.setName(alias);
		}
		
		return functionProjection;
	}

	private String getFunctionResultType(List<ProjectionItem> fromProjections) throws AmbiguousCoalesceException {
		if (function.isAllColumns()) {
			return determineFunctionType(null);
		} else {
			List<String> paramsTypes = Lists.newArrayList();
			
			ExpressionList functionParams = function.getParameters();
			if (functionParams != null) {
				List<Expression> params = functionParams.getExpressions();
				
				for (Expression param : params) {
					paramsTypes.add(getTypeFromSubExpression(param, fromProjections));
				}
			}
			
			return determineFunctionType(paramsTypes);
		}
	}
	
	private String determineFunctionType(List<String> paramsTypes) throws AmbiguousCoalesceException {
		String lFunctionName = function.getName().toLowerCase();
		
		if (lFunctionName.equals("sum")) {
			assertLen1List(paramsTypes);
			return paramsTypes.get(0);
		} else if (lFunctionName.equals("count")) {
			return "java.lang.Integer";
		} else if (lFunctionName.equals("max")) {
			assertLen1List(paramsTypes);
			return paramsTypes.get(0);
		} else if (lFunctionName.equals("min")) {
			assertLen1List(paramsTypes);
			return paramsTypes.get(0);
		} else if (lFunctionName.equals("last_insert_id")) {
			return "java.lang.Long";
		} else if (lFunctionName.equals("coalesce")) {
			assertLenNParams(paramsTypes, 2);
			String param1Type = paramsTypes.get(0);
			String param2Type = paramsTypes.get(1);
			
			if (param1Type.equals(param2Type)) {
				return param1Type;
			} else {
				throw new AmbiguousCoalesceException(function.toString(), param1Type, param2Type);
			}
		} else if (lFunctionName.equals("current_date")) {
			return "java.sql.Date";
		} else if (lFunctionName.equals("datediff")) {
			return "java.lang.Long";
		}
		
		return null;
	}
}
