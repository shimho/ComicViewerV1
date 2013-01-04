package com.example.comicviewerv1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.io.FilenameUtils;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipInputStream;

public class ReadingBookInfo {
	
	// absolute path of folder or zip file
	private String mBookPath;
	private int mSteps = 1;   // 1 for normal, 2 for 2pages view mode

	// zip files
	// if multiple files are in folder, it has multiple paths
	private ArrayList<IndexedString> mBookFiles = null;
	private int mCurrentBookIndex = -1;  
	
	// pages for the current book
	private ArrayList<IndexedString> mBookPages = null;
	private int mCurrentPageIndex = -1;
	
	public ReadingBookInfo(String bookFilePath) {
		this.mBookPath = bookFilePath;
		this.mBookFiles = new ArrayList<IndexedString>();
		this.mBookPages = new ArrayList<IndexedString>();
	}
	
	public String getBookPath() {
		return mBookFiles.get(mCurrentBookIndex).getString();
	}
	
	public String getPageFile() {
		return mBookPages.get(mCurrentPageIndex).getString();
	}
	
	public String getNextPageFile() {
		if (mCurrentPageIndex+1 < mBookPages.size()) {
			return mBookPages.get(mCurrentPageIndex+1).getString();
		}
		return "";
	}
	
	public String getBookTitle() {
		return FilenameUtils.getBaseName(mBookPath);
	}
	
	public int getTotalBooks() {
		return mBookFiles.size();
	}
	
	public int getTotalPages() {
		return mBookPages.size();
	}
	
	public int getCurrentBookIndex() {
		return mCurrentBookIndex;
	}
	
	public int getCurrentPageIndex() {
		return mCurrentPageIndex;
	}
	
	public void setShowTwoPages(boolean flag) {
		mSteps = flag ? 2 : 1;
	}
	
	public boolean load(int bookIndex, int pageIndex) {
		File topf = new File(mBookPath);
		mCurrentBookIndex = bookIndex;
		mCurrentPageIndex = pageIndex;
		if (topf.isDirectory()) {
			if (!loadFolderBook(topf)) 
				return false;
			
		} else if (ComicUtil.isZipFile(mBookPath)) {
			mBookFiles.add(new IndexedString(mBookPath));
			if (!loadZipBook(mBookPath)) 
				return false;
		} else {
			// not applicable book file
			return false;
		}
				
		return true;
	}
	
	private boolean loadZipBook(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
	        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	        ZipEntry entry;
	        while((entry = zis.getNextEntry()) != null) {
	        	String filename = entry.getName();	            
	            mBookPages.add(new IndexedString(filename));
	        }
	        zis.close();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
	    }
		
		Collections.sort(mBookPages, new IndexedStringComparator());
		
		if (mCurrentPageIndex >= mBookPages.size()) {
			// reset if invalid page index
			mCurrentPageIndex = 0;
		}
		return true;
	}
	
	private boolean loadFolderBook(File topf) {
		File[] subfList = topf.listFiles();
		for (int i=0; i<subfList.length; i++) {
			File subf = subfList[i];
			if (!ComicUtil.isZipFile(subf.getAbsolutePath())) {
				continue;
			}

			mBookFiles.add(new IndexedString(subf.getAbsolutePath()));
		}

		if (mBookFiles.size() < 1) {
			return false;
		}
		
		Collections.sort(mBookFiles, new IndexedStringComparator());
		
		if (mCurrentBookIndex >= mBookFiles.size()) {
			// reset if invalid book index passed
			mCurrentBookIndex = 0;
			mCurrentPageIndex = 0;
		}
		// load the first zip file pages
		if (!loadZipBook(mBookFiles.get(0).getString())) {
			return false;
		}
		return true;
	}
	
	public boolean nextPage(int n) {
		if (mCurrentPageIndex >= mBookPages.size()-mSteps) {
			return false;
		}
		
		if (mCurrentPageIndex + mSteps*n < mBookPages.size()) {
			mCurrentPageIndex += mSteps*n;
		} else {
			// open last page
			if (mSteps == 1) {
				mCurrentPageIndex = mBookPages.size()-1;
			} else {
				mCurrentPageIndex = ((mBookPages.size()-1)/2)*2;
			}
			
		}
		return true;
	}
	
	public boolean prevPage(int n) {
		if (mCurrentPageIndex == 0) {
			return false;
		}
		
		if (mCurrentPageIndex - mSteps*n >= 0) {
			mCurrentPageIndex -= mSteps*n;
		} else {
			// open first page
			mCurrentPageIndex = 0;
		}
		return true;
	}
	
	public boolean nextBook() {

		if (mCurrentBookIndex+1 >= mBookFiles.size()) {
			return false;
		}
		
		mCurrentBookIndex += 1;
		if (!loadZipBook(mBookFiles.get(mCurrentBookIndex).getString())) {
			return false;
		}
			
		mCurrentPageIndex = 0;
		return true;
	}
	
	public boolean prevBook() {

		if (mCurrentBookIndex-1 < 0) {
			return false;
		}
		
			// if prev book exists, open the last page of prev book
		mCurrentBookIndex -= 1;
		if (!loadZipBook(mBookFiles.get(mCurrentBookIndex).getString())) {
			return false;
		}
		
		mCurrentPageIndex = 0;
		return true;
	}
}
