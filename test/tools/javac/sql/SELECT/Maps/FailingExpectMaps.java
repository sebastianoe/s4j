/*
 * @test  /nodynamiccopyright/
 * @summary Failing Maps
 * @compile/fail/ref=FailingExpectMaps.out FailingExpectMaps.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

public class FailingExpectMaps {
    public static void main(String[] args) {
    	Map<String, String> sqlMap = SQL[SELECT city, name FROM storage];
    }    
}