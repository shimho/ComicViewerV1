package com.example.comicviewerv1;

import android.graphics.Bitmap;

public class BookshelfItem {
	private int id;
	private String title;
	private String fullPath;
	private Bitmap cover;
	private int numOfBooks;
	
	public BookshelfItem(String title, String fullPath,
						Bitmap cover, int numOfBooks) {
		this.title = title;
		this.fullPath = fullPath;
		this.numOfBooks = numOfBooks;
		this.cover = cover;
	}

	public String getTitle() {
		return this.title;
	}
	
	public String getFullPath() {
		return this.fullPath;
	}
	
	public Bitmap getCover() {
		return this.cover;
	}
	
	public int getNumOfBooks() {
		return this.numOfBooks;
	}
}
