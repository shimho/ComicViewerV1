package com.example.comicviewerv1;

public class BookshelfItem {
	private int id;
	private String title;
	private String body;
	
	public BookshelfItem(int id, String title, String body) {
		this.id = id;
		this.title = title;
		this.body = body;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getBody() {
		return body;
	}
}