/*
 * @test  /nodynamiccopyright/
 * @summary Simple compile time test.
 * 
 * @compile SimpleCompileTimeTest.java
 */

public class SimpleCompileTimeTest {
    public static String returnAString() {
        return "Hi there";
    }

    public static void main(String[] args) {
        System.out.println(returnAString());
    }    
}