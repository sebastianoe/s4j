/*
 * @test  (c) SO
 * @summary Fail, since table doesn't exist
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile/fail/ref=FailingObjects1.out FailingObjects1.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */

import businessobjects.Product;
import businessobjects.Storage;

public class FailingObjects1 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT * FROM anything];
    }    
}