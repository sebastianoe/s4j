/*
 * @test (c) SO
 * @summary Read objects with renamed / aliased base class (assign a storage entry to a product instance)
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile Objects8.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects8
 */

import java.util.List;

import businessobjects.Product;
import businessobjects.Storage;

public class Objects8 {
    public static void main(String[] args) {
    	List<Product> p = SQL[SELECT * FROM product ORDER BY ID LIMIT 2 ];
    	
    	assert p.size() == 2;
    	
    	Product p1 = p.get(0);
    	assert p1.getId() == 1;
    	assert p1.getName().equals("Chocolate");
    	assert p1.getStorage() != null;
    	assert p1.getStorage().getId() == 2;
    	
    	Product p2 = p.get(1);
    	assert p2.getId() == 2;
    	assert p2.getName().equals("Lollipops");
    	assert p2.getStorage() != null;
    	assert p2.getStorage().getId() == 1;
    }    
}