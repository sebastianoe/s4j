/*
 * @test
 * @summary pass a string variable as a param into the query
 * @compile StringParameter.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main StringParameter
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import com.google.common.collect.Lists;
import java.util.List;

public class StringParameter {
	public static void main(String[] args) throws SQLException {
		String lastName = "Bloomberg";
		List<String> l = Lists.newArrayList();
		
    	ResultSet rs = SQL[SELECT first_name FROM customer WHERE last_name LIKE $lastName$];
    	
    	rs.next();
    	String firstName = rs.getString("first_name");
    	if (!firstName.equals("Jessica")) {
    		throw new AssertionError("Got the wrong value for first_name");
    	}
	}
}
