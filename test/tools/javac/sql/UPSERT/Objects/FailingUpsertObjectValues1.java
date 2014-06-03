/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts object values - should not compile as Product class has no city attribute
 * @compile businessobjects/Product.java
 * @compile businessobjects/Storage.java
 * @compile util/SqlConnectionManager.java
 * @compile util/DMLDataManager.java
 * @compile/fail/ref=FailingUpsertObjectValues1.out FailingUpsertObjectValues1.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;

public class FailingUpsertObjectValues1 {
    public static void main(String[] args) {
    	SQL[UPSERT product (name, storage_id) VALUES ($"MyName"$, $3$) WITH PRIMARY KEY];
    }
}