/*
 * @test
 * @compile/fail/ref=InvalidProjection1.out InvalidProjection1.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
// key: compiler.err.sql.validation.types.not.compatible
import java.sql.ResultSet;

public class InvalidProjection1 {
	public static void main(String[] args) {
		int i = SQL[SELECT 3.14];
	}
}