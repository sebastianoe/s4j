/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * @compile RuntimeExpectResultSet.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main RuntimeExpectResultSet
 */

import java.sql.ResultSet;
import java.sql.SQLException;

public class RuntimeExpectResultSet {
    public static void main(String[] args) throws SQLException {
        ResultSet rsCustomers = SQL[SELECT first_name, last_name FROM customer WHERE first_name = 'Homer'];
        rsCustomers.next();
        assert(rsCustomers.getString("last_name").equals("Simpson"));
    }    
}