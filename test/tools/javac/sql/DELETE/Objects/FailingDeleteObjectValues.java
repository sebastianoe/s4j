/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts object values
 * @compile/fail/ref=FailingDeleteObjectValues.out FailingDeleteObjectValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;
import businessobjects.Storage;

public class FailingDeleteObjectValues {
    public static void main(String[] args) {
    	Object anyObject = new Object();
        SQL[DELETE FROM product WHERE $anyObject$];
    }
}