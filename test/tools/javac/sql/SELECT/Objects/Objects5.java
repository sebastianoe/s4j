/*
 * @test (c) SO
 * @summary Read objects with multiple fk members (storage & anotherStorage)
 * @compile businessobjects/Product2.java
 * @compile businessobjects/Storage.java
 * @compile Objects5.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects5
 */

import businessobjects.Product2;
import businessobjects.Storage;

public class Objects5 {
    public static void main(String[] args) {
    	Product2 p = SQL[SELECT * FROM product product2 JOIN storage ON product2.storage_id = storage.id JOIN storage anotherStorage ON product2.storage_id = anotherStorage.id - 1 WHERE product2.id = 1];
    	
    	assert p.getId() == 1;
    	assert p.getName().equals("Chocolate");
    	
    	Storage s = p.getStorage();
    	assert s != null;
    	assert s.getId() == 2;
    	assert s.getName().equals("NY Power Storage");
    	assert s.getCity().equals("New York City");
    	
    	Storage s2 = p.getAnotherStorage();
    	assert s2 != null;
    	assert s2.getId() == 3;
    	assert s2.getName().equals("Super Storage Plus");
    	assert s2.getCity().equals("Salt Lake City");
    }    
}