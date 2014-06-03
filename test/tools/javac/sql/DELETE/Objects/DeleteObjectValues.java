/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts object values
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile util/SqlConnectionManager.java
 * @compile util/DMLDataManager.java
 * @compile DeleteObjectValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main DeleteObjectValues
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;
import businessobjects.Storage;

public class DeleteObjectValues {
    public static void main(String[] args) {
        List<Product> products = SQL[SELECT * FROM product];
        assert(products.size() == 5);
    	
        // the insert
        Product product = new Product(6, "Donut", new Storage(1, null, null));
    	SQL[INSERT INTO product VALUES $product$];
    	
    	products = SQL[SELECT * FROM product];
        assert(products.size() == 6);
    			
        // cleanup
        SQL[DELETE FROM product WHERE $product$];
        
        products = SQL[SELECT * FROM product];
        assert(products.size() == 5);
    }
}