/*
 * @test  /nodynamiccopyright/
 * @summary Assign to an existing String variable
 * @compile StringAssignment.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main StringAssignment
 */

public class StringAssignment {
    public static void main(String[] args) {
    	String productName = ":(";
    	productName = SQL[SELECT name FROM product WHERE id = 1];

    	assert(productName.equals("Chocolate"));
    }    
}