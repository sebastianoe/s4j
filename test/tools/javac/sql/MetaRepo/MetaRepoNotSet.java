/*
 * @test  /nodynamiccopyright/
 * @summary Throw error when meta repo is not set but needed
 * @compile/fail/ref=MetaRepoNotSet.out MetaRepoNotSet.java
 */
// key: compiler.err.sql.metarepo.not.available
public class MetaRepoNotSet {
    public static void main(String[] args) {
    	Integer i1 = SQL[SELECT 42 FROM line_item];
    }
}

