/*
 * @test (c) SO
 * @summary Read objects with renamed / aliased base class (assign a storage entry to a product instance)
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile Objects7.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects7
 */

import businessobjects.Product;
import businessobjects.Storage;

public class Objects7 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT product.id, product.name FROM storage AS product WHERE id = 1];
    	
    	assert p.getId() == 1;
    	assert p.getName().equals("LA Storage");
    	assert p.getStorage() == null;
    }    
}