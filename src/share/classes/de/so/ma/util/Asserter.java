package de.so.ma.util;

import java.util.List;

/**
 * Utility class to provide some assertion shortcuts.
 * @author Sebastian Oergel
 *
 */
public class Asserter {
	/**
	 * Asserts that the list <code>params</code> contains exactly <code>n</code> items.
	 * @param params
	 * @param n
	 */
	public static void assertLenNParams(List<?> params, int n) {
		if (params.size() != n) {
			// tbd: better exception handling
			throw new RuntimeException("Length of params is not " + n);
		}
	}
	
	/**
	 * Asserts that the list <code>params</code> contains exactly 1 item.
	 * @param params
	 */
	public static void assertLen1List(List<?> params) {
		assertLenNParams(params, 1);
	}
}
