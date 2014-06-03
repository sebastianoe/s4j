/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * @compile RuntimeExpectInteger.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main RuntimeExpectInteger
 */

public class RuntimeExpectInteger {
    public static void main(String[] args) {
        int qty1 = SQL[SELECT 42 FROM line_item];
        assert(qty1 == 42);
        
        int qty2 = SQL[SELECT quantity FROM line_item WHERE id = 1];
        assert(qty2 == 12);
        
        int qty3 = SQL[SELECT SUM(quantity) FROM line_item WHERE id <= 3];
        assert(qty3 == 34);
        
        int qty4 = SQL[SELECT quantity FROM line_item ORDER BY id];
        assert(qty4 == 12);
        
        int qty5 = SQL[SELECT quantity FROM line_item ORDER BY id DESC];
        assert(qty5 == 31);
    }    
}