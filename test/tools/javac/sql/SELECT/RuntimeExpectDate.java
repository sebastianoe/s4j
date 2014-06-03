/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * @compile RuntimeExpectDate.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main RuntimeExpectDate
 */

public class RuntimeExpectDate {
    public static void main(String[] args) {
    	java.sql.Date firstBDate = SQL[SELECT birth_date FROM customer WHERE ID=1];
    	assert(firstBDate.toString().equals("1978-04-26"));
    	
    	java.util.Date utilDate = SQL[SELECT birth_date FROM customer WHERE ID=1];
    	assert(utilDate.toString().equals("1978-04-26"));
    }    
}