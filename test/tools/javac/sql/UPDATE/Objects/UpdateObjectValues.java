/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts object values
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile util/SqlConnectionManager.java
 * @compile util/DMLDataManager.java
 * @compile UpdateObjectValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main UpdateObjectValues
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;
import businessobjects.Storage;

public class UpdateObjectValues {
    public static void main(String[] args) {    	
    	int productCount = SQL[SELECT COUNT(*) FROM product];
    	assert(productCount == 5);
    	
    	// dummy insert
    	Product product = new Product(6, "Donut", new Storage(1, null, null));
        SQL[INSERT INTO product VALUES $product$];
        productCount = SQL[SELECT COUNT(*) FROM product];
        assert(productCount == 6);
    	
    	// update
        product.setName("Doughnut");
        product.getStorage().setId(2);
        SQL[UPDATE product SET $product$];
        
        // validation
        Product updatedProduct = SQL[SELECT * FROM product WHERE ID=6];
        assert(updatedProduct.getName().equals("Doughnut"));
        assert(updatedProduct.getStorage().getId() == 2);
        
        // cleanup
        SQL[DELETE FROM product WHERE ID=6];
        assert(SQL{int}[SELECT COUNT(*) FROM product] == 5);
    }
}