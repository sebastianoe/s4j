/*
 * @test  (c) SO
 * @summary Fail due to unassignable projection
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile/fail/ref=FailingObjects2.out FailingObjects2.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */

import businessobjects.Product;
import businessobjects.Storage;

public class FailingObjects2 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT * FROM customer];
    }    
}