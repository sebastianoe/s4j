/*
 * @test
 * @summary String is a valid param here, as MySQL handles this as 0. Thus, it should contain some elements
 * @compile ValidParam.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main ValidParam
 */

import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidParam {
	public static void main(String[] args) throws SQLException {
		String foo = "foo";
		ResultSet rs = SQL[SELECT * FROM line_item WHERE quantity > $foo$];
		assert(rs.next());
		assert(rs.next());
		assert(rs.next());
	}
}