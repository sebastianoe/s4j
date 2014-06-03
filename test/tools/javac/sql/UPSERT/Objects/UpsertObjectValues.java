/*
 * @test  /nodynamiccopyright/
 * @summary Upsert statement that upserts object values
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile util/SqlConnectionManager.java
 * @compile util/DMLDataManager.java
 * @compile UpsertObjectValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main UpsertObjectValues
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;
import businessobjects.Storage;

public class UpsertObjectValues {
    public static void main(String[] args) {
        assert(5 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
        Product dummyProduct = new Product(6, "Donut", new Storage(2, null, null));
        // the upsert
    	SQL[UPSERT product VALUES $dummyProduct$ WITH PRIMARY KEY];
    	assert(6 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
    	Product p = SQL[SELECT * FROM product WHERE ID=6];
    	assert(p.getName().equals("Donut"));
    	assert(p.getStorage().getId() == 2);
    	
    	dummyProduct.setName("Doughnut");
    	dummyProduct.getStorage().setId(3);
    	
    	// another upsert
    	SQL[UPSERT product VALUES $dummyProduct$ WITH PRIMARY KEY];
    	assert(6 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
    	p = SQL[SELECT * FROM product WHERE ID=6];
    	assert(p.getName().equals("Doughnut"));
    	assert(p.getStorage().getId() == 3);
    	
        // cleanup
        DMLDataManager.deleteModifiedEntries();
        assert(5 == SQL{int}[SELECT COUNT(*) FROM product]);
    }
}