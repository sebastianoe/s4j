/*
 * @test
 * @summary pass a long variable as a param into the query
 * @compile LongParameter.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main LongParameter
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import com.google.common.collect.Lists;
import java.util.List;

public class LongParameter {
	public static void main(String[] args) throws SQLException {
		long cId = 5;
		
    	ResultSet rs = SQL[SELECT first_name FROM customer WHERE id = $cId$];
    	
    	rs.next();
    	String firstName = rs.getString("first_name");
    	if (!firstName.equals("Homer")) {
    		throw new AssertionError("Got the wrong value for first_name");
    	}
	}
}
