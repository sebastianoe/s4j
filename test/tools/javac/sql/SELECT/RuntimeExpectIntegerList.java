/*
 * @test  /nodynamiccopyright/
 * @summary Lists of simple types (here: Integer)
 * @compile RuntimeExpectIntegerList.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 * @run main RuntimeExpectIntegerList
 */

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

public class RuntimeExpectIntegerList {
    public static void main(String[] args) {
    	ArrayList<Integer> expectedList = Lists.newArrayList(12, 5, 17);
    	
    	// Concrete list expected
    	ArrayList<Integer> qties1 = SQL[SELECT quantity FROM line_item ORDER BY id LIMIT 3];
        assert(qties1.size() == 3);
        assert(Iterables.elementsEqual(qties1, expectedList));
        
        // Abstract list expected
        List<Integer> qties2 = SQL[SELECT quantity FROM line_item ORDER BY id LIMIT 3];
        assert(qties2.size() == 3);
        assert(Iterables.elementsEqual(qties2, expectedList));
        assert(qties2 instanceof ArrayList);
    }    
}