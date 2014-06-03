/*
 * @test  /nodynamiccopyright/
 * @summary Don't compile correctly, as meta repo is not set
 * @compile/fail/ref=MetaRepoNotSet.out MetaRepoNotSet2.java
 */
// key: compiler.err.sql.metarepo.not.available

public class MetaRepoNotSet2 {
    public static void main(String[] args) {
    	Integer i1 = SQL[SELECT 42 FROM line_item];
    }    
}

