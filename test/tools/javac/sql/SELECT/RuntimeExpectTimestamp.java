/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * @compile RuntimeExpectTimestamp.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main RuntimeExpectTimestamp
 */

public class RuntimeExpectTimestamp {
    public static void main(String[] args) {
    	java.sql.Timestamp loginTimestamp = SQL[SELECT last_login_ts FROM customer WHERE ID=1];
    	System.out.println(loginTimestamp);
    	assert(loginTimestamp.toString().equals("2013-10-01 08:57:15.0"));
    }    
}