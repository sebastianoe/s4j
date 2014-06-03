/*
 * @test (c) SO
 * @summary Read objects without hierarchy level
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile Objects3.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects3
 */

import businessobjects.Product;
import businessobjects.Storage;

public class Objects3 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT id, name FROM product WHERE product.id = 1];
    	
    	assert p.getId() == 1;
    	assert p.getName().equals("Chocolate");
    	
    	Storage s = p.getStorage();
    	assert s == null;
    }    
}