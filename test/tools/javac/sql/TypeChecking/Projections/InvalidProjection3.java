/*
 * @test
 * @compile/fail/ref=InvalidProjection3.out InvalidProjection3.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
// key: compiler.err.sql.validation.types.not.compatible
import java.sql.ResultSet;

public class InvalidProjection3 {
	public static void main(String[] args) {
		Integer i = SQL[SELECT first_name FROM customer LIMIT 1];
	}
}