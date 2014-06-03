/*
 * @test (c) SO
 * @summary Read objects without hierarchy level but with fk in star
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile Objects2.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects2
 */

import businessobjects.Product;
import businessobjects.Storage;

public class Objects2 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT * FROM product WHERE product.id = 1];
    	
    	assert p.getId() == 1;
    	assert p.getName().equals("Chocolate");
    	
    	Storage s = p.getStorage();
    	assert s != null;
    	assert s.getId() == 2;
    	assert s.getName() == null;
    	assert s.getCity() == null;
    }    
}