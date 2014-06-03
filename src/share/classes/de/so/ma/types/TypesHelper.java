package de.so.ma.types;

import static de.so.ma.util.Asserter.assertLen1List;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTags;
import com.sun.tools.javac.code.Types;

public class TypesHelper {
	private static TypesHelper instance;
	private Map<String, String> jdbcTypeNames;
	
	public static void initializeInstance(Types types) {
		instance = new TypesHelper();
		instance.types = types;
	}
	
	public static TypesHelper getInstance() {
		return instance;
	}
	
	public TypesHelper() {
		initCompatibleTypes();
		initJdbcTypeNames();
	}
	
	private void initJdbcTypeNames() {
		jdbcTypeNames = Maps.newHashMap();
		jdbcTypeNames.put("java.lang.String", "String");
		jdbcTypeNames.put("java.lang.Integer", "Int");
		jdbcTypeNames.put("java.lang.Double", "Double");
		jdbcTypeNames.put("java.lang.Boolean", "Boolean");
		jdbcTypeNames.put("java.lang.Byte", "Byte");
		jdbcTypeNames.put("java.lang.Long", "Long");
		jdbcTypeNames.put("java.lang.Short", "Short");
		jdbcTypeNames.put("java.sql.Date", "Date");
		jdbcTypeNames.put("java.util.Date", "Date");
		jdbcTypeNames.put("java.sql.Time", "Time");
		jdbcTypeNames.put("java.sql.Timestamp", "Timestamp");
	}
	
	private void initCompatibleTypes() {
		compatibleTypes = HashMultimap.create();
		compatibleTypes.putAll("java.lang.Double", 
				Lists.newArrayList("java.lang.Double", "java.lang.Integer"));
		compatibleTypes.put("java.lang.Integer", "java.lang.Integer");
		compatibleTypes.put("java.lang.String", "java.lang.String");
		compatibleTypes.putAll("java.lang.Long", 
				Lists.newArrayList("java.lang.Long", "java.lang.Integer"));
		compatibleTypes.put("java.sql.Date", "java.sql.Date");
		compatibleTypes.put("java.util.Date", "java.sql.Date");
		compatibleTypes.put("java.sql.Time", "java.sql.Time");
		compatibleTypes.put("java.sql.Timestamp", "java.sql.Timestamp");
	}
	
	private Types types;
	private Multimap<String, String> compatibleTypes;
	
	public boolean isListType(Type type) {
		if (type.isPrimitive()) {
			return false;
		}
		
		List<String> closure = getClosure(type);
		return closure.contains("java.util.List");
	}
	
	public boolean isMapType(Type type) {
		if (type.isPrimitive()) {
			return false;
		}
		
		String rawTypeName = type.tsym.toString();
		
		return 
				rawTypeName.equals("java.util.Map") ||
				rawTypeName.equals("java.util.HashMap") ||
				rawTypeName.equals("java.util.LinkedHashMap");
	}

	public boolean isResultSet(Type type) {
		return type.toString().equals("java.sql.ResultSet");
	}

	private List<String> getClosure(Type type) {
		List<String> closure = Lists.newArrayList();
		for (Type closureType : types.closure(type)) {
			closure.add(closureType.tsym.toString());
		}
		
		return closure;
	}
	
	public Type getRawType(Type originalType) {
		if (isListType(originalType)) {
			List<Type> paramTypes = originalType.allparams();
			assertLen1List(paramTypes);
			
			return paramTypes.get(0);
		} else {
			return originalType;
		}
	}
	
	public String getJdbcTypeName(String javaTypeName) {
		return jdbcTypeNames.get(javaTypeName);
	}
	
	public Type getBoxedType(Type type) {
		return types.boxedTypeOrType(type);
	}
	
	public boolean isJdbcType(Type type) {
		return jdbcTypeNames.containsKey(type.toString());
	}
	
	public boolean isSimpleType(Type type) {
		return isJdbcType(getBoxedType(type));
	}
	
	public boolean isNoneType(Type type) {
		return type.tag == TypeTags.NONE;
	}
	
	public boolean areTypesCompatible(Type expectedRawType, String queryTypeName) {
		String expectedTypeName = getBoxedType(expectedRawType).toString();
		
		Collection<String> compatibleToExpected = compatibleTypes.get(expectedTypeName);
		if (compatibleToExpected.contains(queryTypeName)) {
			return true;
		}
		
		return false;
	}
}
