/*
 * @test
 * @compile ValidProjection.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main ValidProjection
 */

import java.sql.ResultSet;

public class ValidProjection {
	public static void main(String[] args) {
		Double d = SQL[SELECT quantity FROM line_item ORDER BY id DESC];
		
		assert(d == 31.0);
	}
}