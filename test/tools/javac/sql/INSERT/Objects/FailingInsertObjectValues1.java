/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts object values - should not compile as Product class has no city attribute
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile util/SqlConnectionManager.java
 * @compile util/DMLDataManager.java
 * @compile/fail/ref=FailingInsertObjectValues1.out FailingInsertObjectValues1.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;

public class FailingInsertObjectValues1 {
    public static void main(String[] args) {
        // the insert
        Product product = new Product(6, "Donut", null);
    	SQL[INSERT INTO storage VALUES $product$];
    }
}