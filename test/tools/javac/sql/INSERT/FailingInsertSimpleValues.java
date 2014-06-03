/*
 * @test  /nodynamiccopyright/
 * @summary Insert statement that inserts simple values
 * @compile/fail/ref=FailingInsertSimpleValues.out FailingInsertSimpleValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class FailingInsertSimpleValues {
    public static void main(String[] args) {
    	SQL[INSERT INTO product VALUES ($"Donut"$, $2$)];
    }
}