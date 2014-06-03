package de.so.ma.helperClasses.filetemplates;

import de.so.ma.metarepo.LiveConnection;
import de.so.ma.metarepo.MetaRepo;

public class SqlConnectionManagerTemplate implements FileTemplate {
	@Override
	public String getClassName() {
		return "SqlConnectionManager";
	}

	@Override
	public String getPackageName() {
		return "de.so.ma";
	}

	@Override
	public String getFileContent() {
		LiveConnection connection = MetaRepo.getInstance().getDB().getLiveConnection();
		
		return String.format("package de.so.ma;\n" + 
				"import java.sql.Connection;\n" + 
				"import java.sql.DriverManager;\n" + 
				"import java.sql.SQLException;\n" + 
				"\n" + 
				"public class SqlConnectionManager {\n" + 
				"	private static Connection connection;\n" + 
				"	\n" + 
				"	public static Connection getConnection() {\n" + 
				"		if (connection == null) {\n" + 
				"			try {\n" + 
				"				Class.forName(\"com.mysql.jdbc.Driver\")\n;" +		
				"	        	String connectionString = \"%s\";\n" + 
				"	            String user = \"%s\";\n" + 
				"	            String password = \"%s\";\n" + 
				"	            connection = DriverManager.getConnection(connectionString, user, password);\n" + 
				"	        } catch(SQLException e) {\n" + 
				"	        	// tbd: better exception handling\n" + 
				"	        	throw new RuntimeException(e);\n" + 
				"	        } catch(ClassNotFoundException e) {\n" +
				"				// tbd: better exception handling\n" +
				"	        	throw new RuntimeException(e);\n" + 
				"			}\n" + 
				"		}\n" + 
				"		\n" + 
				"		return connection;\n" + 
				"	}\n" + 
				"}\n" + 
				"",
				connection.getUrl(), connection.getUser(), connection.getPassword());
	}
	
}
