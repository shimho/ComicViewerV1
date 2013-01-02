package com.example.comicviewerv1;

public class BookPage {
	private long byteCompressedOffset;  // offset in zip file
	private long byteCompressedSize;		// jpg size in compressed format
	private String filename;
	
	public BookPage(String filename, long byteCompressedOffset, long byteCompressedSize) {
		this.filename = filename;
		this.byteCompressedOffset = byteCompressedOffset;
		this.byteCompressedSize = byteCompressedSize;
	}
	
	public long getByteCompressedOffset() { 
		return this.byteCompressedOffset;
	}
	
	public long getByteCompressedSize() {
		return this.byteCompressedSize;
	}
	
	public String getFilename() {
		return this.filename;
	}
}
