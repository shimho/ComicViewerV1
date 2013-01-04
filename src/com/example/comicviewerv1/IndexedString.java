package com.example.comicviewerv1;

import java.util.regex.Pattern;

public class IndexedString {
	private int index = -1;
	private String name;
	
	// TODO, extract index from string, so we can use index for sorting
	public IndexedString(String s) {
		name = s;
	}
	
	public String getString() {
		return name;
	}
	
	/*
	private int extractIndex(String s) {
		Pattern p = Pattern.compile(".*(\d+)");
	}
	*/
	
	
}
