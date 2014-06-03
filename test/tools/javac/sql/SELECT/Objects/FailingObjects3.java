/*
 * @test  (c) SO
 * @ignore
 * @summary Fail due to missing id (note: currently disabled as an explicit id isn't necessarily required)
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile/fail/ref=FailingObjects3.out FailingObjects3.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */

import businessobjects.Product;
import businessobjects.Storage;

public class FailingObjects3 {
    public static void main(String[] args) {
    	Product p = SQL[SELECT * FROM storage WHERE id = 1];
    }
}