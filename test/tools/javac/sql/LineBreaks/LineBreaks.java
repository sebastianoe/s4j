/*
 * @test  /nodynamiccopyright/
 * @summary LineBreaks in SQL statements should work
 * @compile LineBreaks.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main LineBreaks
 */

public class LineBreaks {
    public static void main(String[] args) {
        int qty = SQL[
                      SELECT SUM(quantity) 
                      FROM line_item 
                      WHERE id <= 3];
        assert(qty == 34);
    }    
}