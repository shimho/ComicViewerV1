package com.example.comicviewerv1;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class BookshelfActivity extends Activity {
	
	BookshelfItemAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookshelf);
		
		ArrayList<BookshelfItem> books = new ArrayList<BookshelfItem>();
		
		File f = new File("/sdcard/Comics/");
		File[] fileList = f.listFiles();
		for (int i=0; i<fileList.length; i++) {
			String path = fileList[i].getAbsolutePath();
			String fileType = fileList[i].isDirectory() ? "Directory" : "File"; 
		
				
			books.add(new BookshelfItem(i, fileType, path));
		}

		adapter = new BookshelfItemAdapter(this, R.layout.book_item_row, books);
		ListView lvShelf = (ListView) findViewById(R.id.bookshelf_list);
		lvShelf.setAdapter(adapter);

		
	}
}
