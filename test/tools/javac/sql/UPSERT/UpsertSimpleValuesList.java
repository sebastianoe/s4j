/*
 * @test  /nodynamiccopyright/
 * @summary Upsert statement that inserts simple values list
 * @compile Objects/businessobjects/Storage.java
 * @compile Objects/businessobjects/Product.java
 * @compile Objects/util/SqlConnectionManager.java
 * @compile Objects/util/DMLDataManager.java
 * @compile UpsertSimpleValuesList.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main UpsertSimpleValuesList
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import businessobjects.Product;

public class UpsertSimpleValuesList {
    public static void main(String[] args) {
        assert(5 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
        // the upsert
    	SQL[UPSERT product VALUES ($6$, $"Donut"$, $2$), ($7$, $"Lolly"$, $3$) WITH PRIMARY KEY];
    	assert(7 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
    	Product p = SQL[SELECT * FROM product WHERE ID=6];
    	assert(p.getName().equals("Donut"));
    	assert(p.getStorage().getId() == 2);
    	
    	Product p2 = SQL[SELECT * FROM product WHERE ID=7];
    	assert(p2.getName().equals("Lolly"));
    	assert(p2.getStorage().getId() == 3);
    	
    	// another upsert
    	SQL[UPSERT product VALUES ($6$, $"Doughnut"$, $3$), ($7$, $"Lollypop"$, $2$) WITH PRIMARY KEY];
    	assert(7 == SQL{int}[SELECT COUNT(*) FROM product]);
    	
    	p = SQL[SELECT * FROM product WHERE ID=6];
    	assert(p.getName().equals("Doughnut"));
    	assert(p.getStorage().getId() == 3);
    	
    	p2 = SQL[SELECT * FROM product WHERE ID=7];
    	assert(p2.getName().equals("Lollypop"));
    	assert(p2.getStorage().getId() == 2);
    	
        // cleanup
        DMLDataManager.deleteModifiedEntries();
        assert(5 == SQL{int}[SELECT COUNT(*) FROM product]);
    }
}