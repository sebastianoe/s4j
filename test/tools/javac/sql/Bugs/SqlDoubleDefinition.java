/*
 * @test  /nodynamiccopyright/
 * @summary Make sure that SELECT expressions do not throw an error.
 * @compile SqlDoubleDefinition.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main SqlDoubleDefinition
 */

public class SqlDoubleDefinition {
    public static void main(String[] args) {
    	Integer i1 = SQL[SELECT 42 FROM line_item];
        assert(i1 == 42);
    	
        Integer i2 = SQL[SELECT 42 FROM line_item];
        assert(i2 == 42);
    }    
}

