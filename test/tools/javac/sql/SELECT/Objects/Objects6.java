/*
 * @test (c) SO
 * @summary Read objects with literal projections (without table)
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile Objects6.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects6
 */

import businessobjects.Product;
import businessobjects.Storage;

public class Objects6 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT 3 AS "id", 'Milk' AS "name"];
    	
    	assert p.getId() == 3;
    	assert p.getName().equals("Milk");
    	
    	assert p.getStorage() == null;
    }    
}