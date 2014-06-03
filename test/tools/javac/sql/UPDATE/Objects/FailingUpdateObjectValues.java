/*
 * @test  /nodynamiccopyright/
 * @summary Arbitrary object should be used for UPDATE. Expected to fail due to a missing getter method.
 * @compile/fail/ref=FailingUpdateObjectValues.out FailingUpdateObjectValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import businessobjects.Product;
import businessobjects.Storage;

public class FailingUpdateObjectValues {
    public static void main(String[] args) {
    	Object product = new Object();
        SQL[UPDATE product SET $product$];
    }
}