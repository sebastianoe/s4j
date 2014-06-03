package de.so.ma.util;

public class SqlStringUtil {
	public static String fixWhitespaces(String sqlString) {
		// remove newlines & tabs
		sqlString = sqlString.replaceAll("[\\n\\t]", "");
		
		// remove multiple spaces
		sqlString = sqlString.replaceAll("[ ]+", " ");
		
		return sqlString;
	}
	
	public static String stripName(String original) {
		return original.replaceAll("`", "");
	}
}
