/*
 * @test  /nodynamiccopyright/
 * @summary Validate that OFFSET and LIMIT clause work as expected
 * @compile OffsetLimit.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main OffsetLimit
 */

import java.util.List;

public class OffsetLimit {
    public static void main(String[] args) {
    	
    	List<String> someProductNames = SQL[SELECT name FROM product ORDER BY ID LIMIT $2$ OFFSET $1$];
    	
    	assert(someProductNames.size() == 2);
    	assert(someProductNames.get(0).equals("Lollipops"));
    	assert(someProductNames.get(1).equals("Candy"));
    }    
}