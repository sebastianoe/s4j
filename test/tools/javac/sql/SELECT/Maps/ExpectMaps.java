/*
 * @test  /nodynamiccopyright/
 * @summary Maps
 * @compile ExpectMaps.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main ExpectMaps
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class ExpectMaps {
    public static void main(String[] args) {
    	LinkedHashMap<String, Long> expectedMap = Maps.newLinkedHashMap();
    	expectedMap.put("LA Storage", 2l);
    	expectedMap.put("NY Power Storage", 2l);
    	expectedMap.put("Super Storage Plus", 1l);
    	
    	// Concrete list expected
    	Map<String, Long> sqlMap = SQL[SELECT s.name, COUNT(*) FROM storage s JOIN product p on s.id = p.storage_id GROUP BY s.name ORDER BY s.ID];
        System.out.println(sqlMap);
        System.out.println(expectedMap);
        System.out.println(sqlMap.size());
    	assert(sqlMap.size() == 3);
        assert(Maps.difference(sqlMap, expectedMap).areEqual());
    }    
}