/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts a list of objects
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile util/SqlConnectionManager.java
 * @compile util/DMLDataManager.java
 * @compile InsertObjectListValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main InsertObjectListValues
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import businessobjects.Product;
import businessobjects.Storage;

import com.google.common.collect.Lists;

public class InsertObjectListValues {
    public static void main(String[] args) {
        List<Product> products = SQL[SELECT * FROM product];
        assert(products.size() == 5);
    	
        // the insert
        List<Product> insertProducts = Lists.newArrayList(
				new Product(6, "Donut", new Storage(1, null, null)),
				new Product(7, "Eclair", new Storage(2, null, null))); 

		int res = SQL[INSERT INTO product VALUES $insertProducts$];

    	assert(res == 2);
    	products = SQL[SELECT * FROM product];
        assert(products.size() == 7);
    			
        // cleanup
        DMLDataManager.deleteModifiedEntries();
        
        products = SQL[SELECT * FROM product];
        assert(products.size() == 5);
    }
}