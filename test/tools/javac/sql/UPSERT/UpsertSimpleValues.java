/*
 * @test  /nodynamiccopyright/
 * @summary Upsert statement that inserts simple values
 * @compile Objects/businessobjects/Storage.java
 * @compile Objects/businessobjects/Product.java
 * @compile Objects/util/SqlConnectionManager.java
 * @compile Objects/util/DMLDataManager.java
 * @compile UpsertSimpleValues.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main UpsertSimpleValues
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;

public class UpsertSimpleValues {
    public static void main(String[] args) {
        assert(5 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
        // the upsert
    	SQL[UPSERT product VALUES ($6$, $"Donut"$, $2$) WITH PRIMARY KEY];
    	assert(6 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
    	Product p = SQL[SELECT * FROM product WHERE ID=6];
    	assert(p.getName().equals("Donut"));
    	assert(p.getStorage().getId() == 2);
    	
    	// another upsert
    	SQL[UPSERT product VALUES ($6$, $"Doughnut"$, $3$) WITH PRIMARY KEY];
    	assert(6 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
    	p = SQL[SELECT * FROM product WHERE ID=6];
    	assert(p.getName().equals("Doughnut"));
    	assert(p.getStorage().getId() == 3);
    	
        // cleanup
        DMLDataManager.deleteModifiedEntries();
        assert(5 == SQL{int}[SELECT COUNT(*) FROM product]);
    }
}