/*
 * @test
 * @summary Simple runtime test
 */
public class SimpleRuntimeTest {
	public static void main(String[] args) throws Exception {
		int a = 5;
		if (a < 3) {
			throw new Exception("a has the wrong value");
		}
	}
}
