/*
 * @test  (c) SO
 * @summary Read objects with hierarchy level
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile Objects1.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects1
 */

import businessobjects.Product;
import businessobjects.Storage;

public class Objects1 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT * FROM product JOIN storage ON product.storage_id = storage.id WHERE product.id = 1];
    	
    	assert p.getId() == 1;
    	assert p.getName().equals("Chocolate");
    	
    	Storage s = p.getStorage();
    	assert s != null;
    	assert s.getId() == 2;
    	assert s.getName().equals("NY Power Storage");
    	assert s.getCity().equals("New York City");
    }    
}