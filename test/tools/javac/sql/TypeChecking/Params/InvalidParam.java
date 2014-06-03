/*
 * @test
 * @summary Param is not accepted due to its type. We don't know how to put it to the DB.
 * @compile/fail/ref=InvalidParam.out InvalidParam.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */
// key: compiler.err.sql.validation.param.type.no.jdbctype
import java.sql.ResultSet;
import java.sql.SQLException;

public class InvalidParam {
	public static void main(String[] args) throws SQLException {
		// we can't pass a StringBuilder as an SQL param 
		StringBuilder sb = new StringBuilder();
		ResultSet rs = SQL[SELECT * FROM line_item WHERE quantity > $sb$];
	}
}