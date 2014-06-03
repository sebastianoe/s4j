/*
 * @test (c) SO
 * @summary Read objects without hierarchy level, with fk, and without star 
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile Objects4.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects4
 */

import businessobjects.Product;
import businessobjects.Storage;

public class Objects4 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT id, storage_id FROM product WHERE product.id = 1];
    	
    	assert p.getId() == 1;
    	assert p.getName() == null;
    	
    	Storage s = p.getStorage();
    	assert s != null;
    	assert s.getId() == 2;
    }    
}