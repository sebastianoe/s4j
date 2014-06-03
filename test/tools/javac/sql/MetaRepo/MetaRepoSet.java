/*
 * @test
 * @summary Compile sql queries correctly when meta repo argument is set
 * @compile MetaRepoSet.java -sqlrepo /Users/sebastian/ma/jdk7u-langtools/test/tools/javac/sql/repo.json
 */

public class MetaRepoSet {
    public static void main(String[] args) {
    	Integer i1 = SQL[SELECT 42 FROM line_item];
    }    
}

