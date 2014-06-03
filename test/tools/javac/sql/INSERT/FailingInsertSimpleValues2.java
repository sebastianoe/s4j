/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts simple values
 * @compile/fail/ref=FailingInsertSimpleValues2.out FailingInsertSimpleValues2.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class FailingInsertSimpleValues2 {
    public static void main(String[] args) {
    	SQL[INSERT INTO product (name, storage_id) VALUES ($1$, $"Donut"$, $2$)];
    }
}