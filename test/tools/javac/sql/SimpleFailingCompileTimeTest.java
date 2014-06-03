/*
 * @test  /nodynamiccopyright/
 * @summary Simple failing compile time test. It is expected to fail compiling.
 * 
 * @compile/fail SimpleFailingCompileTimeTest.java
 */

public class SimpleFailingCompileTimeTest {
    public static String returnAString() {
    	return 42; // 42 is not a String
    }

    public static void main(String[] args) {
        System.out.println(returnAString());
    }    
}