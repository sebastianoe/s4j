/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * @compile BinaryExpressionTransformation.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main BinaryExpressionTransformation
 */

public class BinaryExpressionTransformation {
    public static void main(String[] args) {
    	int incrementedProductRowCount = SQL{int}[SELECT COUNT(*) FROM product] + 1;
    	
    	assert (incrementedProductRowCount == 6);
    }    
}

