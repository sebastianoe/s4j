/*
 * @test  /nodynamiccopyright/
 * @summary Maps 4
 * @compile ExpectMaps4.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main ExpectMaps4
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class ExpectMaps4 {
    public static void main(String[] args) {
    	HashMap<String, Long> expectedMap = Maps.newHashMap();
    	expectedMap.put("Chicago", 1l);
    	expectedMap.put("Denver", 1l);
    	expectedMap.put("Los Angeles", 2l);
    	expectedMap.put("Milwaukee", 1l);
    	expectedMap.put("Springfield", 1l);
    	
    	// Concrete list expected
    	Map<String, Long> sqlMap = SQL[SELECT city, COUNT(*) FROM customer GROUP BY city];
        assert(sqlMap.size() == 5);
        assert(Maps.difference(sqlMap, expectedMap).areEqual());
    }    
}