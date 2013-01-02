package com.example.comicviewerv1;

import java.util.Comparator;


public class BookPageComparator implements Comparator<BookPage> {
	@Override
	public int compare(BookPage page1, BookPage page2) {
		return page1.getFilename().compareTo(page2.getFilename());
	}
	
}
