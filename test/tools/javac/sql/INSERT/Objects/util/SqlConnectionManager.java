import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnectionManager {
	private static Connection connection;
	
	public static Connection getConnection() {
		if (connection == null) {
			try {
	        	String connectionString = "jdbc:mysql://localhost:3306/ma";
	            String user = "root";
	            String password = "";
	            connection = DriverManager.getConnection(connectionString, user, password);
	        } catch(SQLException e) {
	        	// tbd: better exception handling
	        	throw new RuntimeException(e);
	        }
		}
		
		return connection;
	}
}
