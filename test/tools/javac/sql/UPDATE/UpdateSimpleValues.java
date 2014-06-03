/*
 * @test  /nodynamiccopyright/
 * @summary Update an already-inserted value
 * @compile Objects/businessobjects/Storage.java
 * @compile Objects/businessobjects/Product.java
 * @compile Objects/util/SqlConnectionManager.java
 * @compile Objects/util/DMLDataManager.java
 * @compile UpdateSimpleValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main UpdateSimpleValues
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Storage;
import businessobjects.Product;

public class UpdateSimpleValues {
    public static void main(String[] args) {
    	int productCount = SQL[SELECT COUNT(*) FROM product];
    	assert(productCount == 5);
    	
    	// dummy insertion
    	SQL[INSERT INTO product VALUES ($6$, $"Donut"$, $2$)];
    	
    	// update the value
    	int updateCount = SQL[UPDATE product SET name=$"Doughnut"$, storage_id=$3$ WHERE ID=$6$];
    	
    	productCount = SQL[SELECT COUNT(*) FROM product];
        assert(productCount == 6);
        
        Product updatedProduct = SQL[SELECT * FROM product WHERE ID=6];
        assert(updatedProduct.getName().equals("Doughnut"));
        assert(updatedProduct.getStorage().getId() == 3);
        
        // deletion
        int deletionId = 6;
        SQL[DELETE FROM product WHERE id = $deletionId$];
        
        productCount = SQL[SELECT COUNT(*) FROM product];
        assert(productCount == 5);
    }
}