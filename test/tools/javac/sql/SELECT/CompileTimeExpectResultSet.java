/*
 * @test  /nodynamiccopyright/
 * @summary Simple compile time test.
 *
 * @compile CompileTimeExpectResultSet.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
import java.sql.ResultSet;

public class CompileTimeExpectResultSet {
    public static void main(String[] args) {
    	ResultSet rs = SQL[SELECT 42];
    }    
}