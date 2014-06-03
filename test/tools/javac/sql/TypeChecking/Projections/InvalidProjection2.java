/*
 * @test
 * @compile/fail/ref=InvalidProjection2.out InvalidProjection2.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
// key: compiler.err.sql.validation.types.not.compatible
import java.sql.ResultSet;

public class InvalidProjection2 {
	public static void main(String[] args) {
		String s = SQL[SELECT quantity FROM line_item];
	}
}