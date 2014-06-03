/*
 * @test  /nodynamiccopyright/
 * @summary Maps 3
 * @compile ExpectMaps3.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main ExpectMaps3
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class ExpectMaps3 {
    public static void main(String[] args) {
    	HashMap<String, String> expectedMap = Maps.newHashMap();
    	expectedMap.put("LA Storage", "Los Angeles");
    	expectedMap.put("NY Power Storage", "New York City");
    	expectedMap.put("Super Storage Plus", "Salt Lake City");
    	
    	// Concrete list expected
    	Map<String, String> sqlMap = SQL[SELECT name, city FROM storage ORDER BY ID];
        assert(sqlMap.size() == 3);
        assert(Maps.difference(sqlMap, expectedMap).areEqual());
    }    
}