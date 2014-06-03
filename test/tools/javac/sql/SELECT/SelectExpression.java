/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * 
 * @compile SelectExpression.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main SelectExpression
 */

public class SelectExpression {
    public static void main(String[] args) {
        int value = SQL[SELECT 42 FROM customer];
        
        if (value != 42) {
        	throw new AssertionError("returned int is not 42");
        }
    }    
}