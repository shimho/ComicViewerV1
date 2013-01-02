package com.example.comicviewerv1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import org.apache.commons.io.FilenameUtils;

public class BookshelfActivity extends Activity {
	
	BookshelfItemAdapter adapter;
	ArrayList<BookshelfItem> books;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookshelf);
		
		books = new ArrayList<BookshelfItem>();
		File f = new File(Environment.getExternalStorageDirectory().getPath() + "/Comics/");
		File[] fileList = f.listFiles();
		int pos = 0;
		for (int i=0; i<fileList.length; i++) {
			BookshelfItem entry = processFile(fileList[i]);
			if (entry == null) continue;
			books.add(entry);
			pos ++;
		}

		adapter = new BookshelfItemAdapter(this, R.layout.book_item_row, books);
		GridView lvShelf = (GridView) findViewById(R.id.bookshelf_grid);
		lvShelf.setAdapter(adapter);
		lvShelf.setOnItemClickListener(bookClickListener);
	}
	
	private BookshelfItem processFile(File f) {
		int numOfBooks = 0;
		String title, fullPath;
		Bitmap cover = null;
		if (f.isDirectory()) {
			// check how many zip files are  in there
			File[] subfList = f.listFiles();
			for (int i=0; i<subfList.length; i++) {
				File subf = subfList[i];
				if (subf.isDirectory()) {
					continue;
				}
				
				if (!FilenameUtils.getExtension(subf.getAbsolutePath()).equalsIgnoreCase("zip")) {
					continue;
				}
				
				numOfBooks++;
				
				// get cover image from the first zip
				if (cover == null) {
					cover = getCoverImage(subf.getAbsolutePath());
				}
			}
			
			if (numOfBooks == 0) {
				// no book found in this folder. skip
				return null;
			}
			
			title = f.getName();
			fullPath = f.getAbsolutePath();
			
		} else {
			// check if it's zip file
			fullPath = f.getAbsolutePath();
			if (!FilenameUtils.getExtension(fullPath).equalsIgnoreCase("zip")) {
				return null;
			}			
			title = FilenameUtils.getBaseName(f.getName());
			
			numOfBooks = 1;
		}
		
		return new BookshelfItem(title, fullPath, cover, numOfBooks);
	}
	

	private OnItemClickListener bookClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView<?> adapterView, View clickedView, int pos, long id)
        {        	 
            Intent intent = new Intent();
            intent.putExtra("bookFilePath", books.get(pos).getFullPath());
            setResult(RESULT_OK, intent);
        	finish();
        }
    };
    
    /**
     * Find the first image as book cover
     * case1 : bookPath is zip of images
     * case2 : bookPath is zip of zip of images
     * case3 : bookPath is folder of zip
     */
    private Bitmap getCoverImage(String bookPath) {
    	return null;
    }
    
    final int BUFFER = 2048;
	final int MAX_JPG_SIZE = 1024*1024;

    private Bitmap getCoverImageOfZip(String bookPath) {
    	try {
			int zipOffset = 0;
	        FileInputStream fis = new FileInputStream(bookPath);
	        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	        ZipEntry entry;
	        while((entry = zis.getNextEntry()) != null) {
	        	String filename = entry.getName();
	        		        	
/*	        	int jpgByte = 0;
	        	byte jpgData[] = new byte[MAX_JPG_SIZE];
	        	
	        	int bufReadByte;
	        	byte buf[] = new byte[BUFFER];
	            while ((bufReadByte = zis.read(buf, 0, BUFFER)) != -1) {
	            	System.arraycopy(buf, 0, jpgData, jpgByte, bufReadByte);
	            	jpgByte += bufReadByte;
	            }
	            
	            break;
	            
	            int zipSize = (int)entry.getSize();
	            
	            mBookPages.add(new BookPage(filename, zipOffset, zipSize));
	            zipOffset += zipSize; */
	        }
	        zis.close();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
	    }
    	return null;
    }


}
