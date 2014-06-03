/*
 * @test  /nodynamiccopyright/
 * @summary Upsert statement that upserts a list of object values
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile util/SqlConnectionManager.java
 * @compile util/DMLDataManager.java
 * @compile UpsertObjectValuesList.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main UpsertObjectValuesList
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;
import businessobjects.Storage;
import com.google.common.collect.Lists;

public class UpsertObjectValuesList {
    public static void main(String[] args) {
        try {
        	assert(5 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
	        List<Product> products = Lists.newArrayList(
	        		new Product(6, "Donut", new Storage(2, null, null)),
	        		new Product(7, "Lolly", new Storage(3, null, null))
	        );
	        // the upsert
	    	
	        SQL[UPSERT product VALUES $products$ WITH PRIMARY KEY];
	    	assert(7 == SQL{int}[SELECT COUNT(*) FROM product]);
	    	
	    	Product p = SQL[SELECT * FROM product WHERE ID=6];
	    	assert(p.getName().equals("Donut"));
	    	assert(p.getStorage().getId() == 2);
	    	
	    	Product p2 = SQL[SELECT * FROM product WHERE ID=7];
	    	assert(p2.getName().equals("Lolly"));
	    	assert(p2.getStorage().getId() == 3);
	    	
	    	products.get(0).setName("Doughnut");
	    	products.get(0).getStorage().setId(3);
	    	
	    	products.get(1).setName("Lollypop");
	    	products.get(1).getStorage().setId(2);
	    	
	    	// another upsert
	    	SQL[UPSERT product VALUES $products$ WITH PRIMARY KEY];
	    	assert(7 == SQL{int}[SELECT COUNT(*) FROM product]);
	    	
	    	p = SQL[SELECT * FROM product WHERE ID=6];
	    	assert(p.getName().equals("Doughnut"));
	    	assert(p.getStorage().getId() == 3);
	    	
	    	p2 = SQL[SELECT * FROM product WHERE ID=7];
	    	assert(p2.getName().equals("Lollypop"));
	    	assert(p2.getStorage().getId() == 2);
        } finally {
	        // cleanup
	        DMLDataManager.deleteModifiedEntries();
	        assert(5 == SQL{int}[SELECT COUNT(*) FROM product]);
        }
    }
}