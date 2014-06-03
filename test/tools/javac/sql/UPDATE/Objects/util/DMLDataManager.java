import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DMLDataManager {
	public static void deleteModifiedEntries() {
		Connection con = SqlConnectionManager.getConnection();
	    try {
	    	PreparedStatement ps = con.prepareStatement("DELETE FROM product WHERE id > 5");
	    	ps.executeUpdate();
	    } catch (SQLException e){
	    	throw new RuntimeException(e);
	    }
	}
}
