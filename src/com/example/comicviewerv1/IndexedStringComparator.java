package com.example.comicviewerv1;

import java.util.Comparator;

public class IndexedStringComparator implements Comparator<IndexedString> {
	@Override
	public int compare(IndexedString obj1, IndexedString obj2) {
		return obj1.getString().compareTo(obj2.getString());
	}
}
