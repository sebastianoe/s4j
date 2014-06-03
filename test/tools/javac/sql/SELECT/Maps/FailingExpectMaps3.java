/*
 * @test  /nodynamiccopyright/
 * @summary Failing Maps 3
 * @compile/fail/ref=FailingExpectMaps3.out FailingExpectMaps3.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;

public class FailingExpectMaps3 {
    public static void main(String[] args) {
    	Map<String, String> sqlMap = SQL[SELECT city, first_name FROM customer];
    }    
}