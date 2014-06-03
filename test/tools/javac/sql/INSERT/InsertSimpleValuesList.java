/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts simple values
 * @compile Objects/businessobjects/Product.java
 * @compile Objects/util/SqlConnectionManager.java
 * @compile Objects/util/DMLDataManager.java
 * @compile InsertSimpleValuesList.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main InsertSimpleValuesList
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;

public class InsertSimpleValuesList {
    public static void main(String[] args) {
        List<Product> products = SQL[SELECT * FROM product];
        assert(products.size() == 5);
    	
        // the insert
    	SQL[INSERT INTO product VALUES ($6$, $"Donut"$, $2$), ($7$, $"Lolly"$, $3$)];
    	
    	products = SQL[SELECT * FROM product];
        assert(products.size() == 7);
    			
        // cleanup
        DMLDataManager.deleteModifiedEntries();
        
        products = SQL[SELECT * FROM product];
        assert(products.size() == 5);
    }
}