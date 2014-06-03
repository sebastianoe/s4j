/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * @compile RuntimeExpectDouble.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main RuntimeExpectDouble
 */

public class RuntimeExpectDouble {
    public static void main(String[] args) {
        double qty1 = SQL[SELECT 42 FROM line_item];
        assert(qty1 == 42.0);
    }    
}