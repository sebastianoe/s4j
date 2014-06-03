package de.so.ma.util;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Strings;
import com.google.common.collect.Table;

import de.so.ma.data.ProjectionItem;
import de.so.ma.validation.matching.flatobject.FlatObjectAttribute;

public class MapPrinter<K, V> {
	public void printMap(Map<K, V> map, Table<ProjectionItem, FlatObjectAttribute, Integer> votings) {
		if (map == null) {
			System.out.println("No map to print");
			return;
		}
		
		int maxKeyLength = getMaxLength(map.keySet());
		int maxValueLength = getMaxLength(map.values());

		for (Entry<K, V> entry : map.entrySet()) {
			System.out.print(Strings.padStart(entry.getKey().toString(), maxKeyLength, ' '));
			System.out.print(" --> ");
			System.out.print(Strings.padEnd(entry.getValue().toString(), maxValueLength, ' '));
			System.out.println(" (" + votings.get(entry.getKey(), entry.getValue()) + ")");
			System.out.println();
		}
	}

	private int getMaxLength(Iterable<?> iterable) {
		int maxLength = 0;

		for (Object o : iterable) {
			int currentLength = o.toString().length();
			if (currentLength > maxLength) {
				maxLength = currentLength;
			}
		}

		return maxLength;
	}

}
