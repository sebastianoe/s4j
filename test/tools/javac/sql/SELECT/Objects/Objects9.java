/*
 * @test (c) SO
 * @summary Put a list of storages into a list attribute of a Product
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile Objects9.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main Objects9
 */

import java.util.List;

import businessobjects.Product;
import businessobjects.Storage;

public class Objects9 {
    public static void main(String[] args) {
    	Product p = new Product();
    	p.setStorageList(SQL{List<Storage>}[SELECT * FROM storage]);
    	
    	assert p != null;
    	assert p.getStorageList() != null;
    	assert p.getStorageList().size() == 3;
    }    
}