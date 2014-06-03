/*
 * @test  (c) SO
 * @summary Fail due to wrong base class: product table needs to be queried or storage would need to be renamed
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile/fail/ref=FailingObjects4.out FailingObjects4.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */

import businessobjects.Product;
import businessobjects.Storage;

public class FailingObjects4 {
    public static void main(String[] args) {
    	Product p = new Product();
    	p.setStorageList(SQL[SELECT * FROM storage]);
    }
}