package de.so.ma.util;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class TablePrinter<R,C,V> {
	public void printTable(Table<R, C, V> table) {
		List<C> columns = Lists.newArrayList(table.columnKeySet());
		int[] columnLengths = getColumnLengths(table);
		
		// print column headers
		System.out.print(Strings.padStart("", columnLengths[0], ' '));
		for (int i = 1; i <= columns.size(); i++) {
			System.out.print(Strings.padStart(columns.get(i - 1).toString(), columnLengths[i], ' '));
			System.out.print("|");
		}
		
		// newline
		System.out.println();
		
		// print rows
		for (R row : table.rowKeySet()) {
			System.out.print(Strings.padStart(row.toString(), columnLengths[0], ' '));
			for (int i = 1; i <= columns.size(); i++) {
				System.out.print(
						Strings.padStart(
								table.get(row, columns.get(i - 1)).toString(), columnLengths[i], ' '));
				System.out.print("|");
			}
			
			// newline
			System.out.println();
		}
	}

	private int[] getColumnLengths(Table<R, C, V> table) {
		int[] lengths = new int[table.columnKeySet().size() + 1];
		List<C> columns = Lists.newArrayList(table.columnKeySet());
		
		for (R row : table.rowKeySet()) {
			int rowLength = row.toString().length();
			if (rowLength > lengths[0]) {
				lengths[0] = rowLength;
			}
			
			for (int i = 1; i <= columns.size(); i++) {
				int columnLength = columns.get(i - 1).toString().length();
				if (columnLength > lengths[i]) {
					lengths[i] = columnLength;
				}
			}
		}
		
		return lengths;
	}
	
	
	
	
}
