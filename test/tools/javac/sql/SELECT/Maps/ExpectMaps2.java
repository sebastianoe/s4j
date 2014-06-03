/*
 * @test  /nodynamiccopyright/
 * @summary Maps 2
 * @compile ExpectMaps2.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main ExpectMaps2
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class ExpectMaps2 {
    public static void main(String[] args) {
    	LinkedHashMap<Long, String> expectedMap = Maps.newLinkedHashMap();
    	expectedMap.put(1l, "LA Storage");
    	expectedMap.put(2l, "NY Power Storage");
    	expectedMap.put(3l, "Super Storage Plus");
    	
    	// Concrete list expected
    	Map<Long, String> sqlMap = SQL[SELECT id, name FROM storage ORDER BY ID];
        assert(sqlMap.size() == 3);
        assert(Maps.difference(sqlMap, expectedMap).areEqual());
    }
}