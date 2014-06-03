/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * @compile RuntimeExpectTime.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main RuntimeExpectTime
 */

public class RuntimeExpectTime {
    public static void main(String[] args) {
    	java.sql.Time loginTime = SQL[SELECT last_login FROM customer WHERE ID=1];
    	System.out.println(loginTime);
    	assert(loginTime.toString().equals("08:57:01"));
    }    
}