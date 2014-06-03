/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts simple values
 * @compile Objects/businessobjects/Storage.java
 * @compile Objects/businessobjects/Product.java
 * @compile Objects/util/SqlConnectionManager.java
 * @compile Objects/util/DMLDataManager.java
 * @compile InsertSimpleValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main InsertSimpleValues
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;

public class InsertSimpleValues {
    public static void main(String[] args) {
        List<Product> products = SQL[SELECT * FROM product];
        assert(products.size() == 5);
    	
        // the insert
    	SQL[INSERT INTO product VALUES ($6$, $"Donut"$, $2$)];
    	
    	products = SQL[SELECT * FROM product];
        assert(products.size() == 6);
    			
        // cleanup
        DMLDataManager.deleteModifiedEntries();
        
        products = SQL[SELECT * FROM product];
        assert(products.size() == 5);
    }
}