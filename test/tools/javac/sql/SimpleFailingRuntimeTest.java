/*
 * @test
 * @summary Simple failing runtime test. This test is expected to fail.
 * @run main/fail SimpleFailingRuntimeTest
 */
public class SimpleFailingRuntimeTest {
	public static void main(String[] args) throws Exception {
		int a = 5;
		if (a < 8) {
			throw new Exception("a has the wrong value");
		}
	}
}
