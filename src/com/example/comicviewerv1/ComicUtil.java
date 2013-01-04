package com.example.comicviewerv1;

import java.io.File;
import java.util.Enumeration;

import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipFile;

import org.apache.commons.io.FilenameUtils;

import android.graphics.Bitmap;

public class ComicUtil {
	public static boolean isImageFile(String path) {
		String ext = FilenameUtils.getExtension(path);
		if (ext.equalsIgnoreCase("jpg") ||
			ext.equalsIgnoreCase("jpeg") ||
			ext.equalsIgnoreCase("png") ||
			ext.equalsIgnoreCase("gif"))
			return true;
		return false;
	}
	
	public static boolean isZipFile(String path) {
		String ext = FilenameUtils.getExtension(path);
		if (ext.equalsIgnoreCase("zip"))
			return true;
		return false;
	}
	
	// a zip file includes other zip files
	// if the first 5 files are not zip file, consider it's not nested zip
	public static boolean isNestedZipFile(String path) {
		try {
			ZipFile zipFile = new ZipFile(new File(path));
			int nonZipFileCount = 0;
	    	for(Enumeration e = zipFile.entries(); 
	    			e.hasMoreElements() && nonZipFileCount < 5; 
	    			nonZipFileCount++){
	    		ZipEntry entry = (ZipEntry)e.nextElement();
	    		String filename = entry.getName();
	    		if (isZipFile(filename)) {
	    			return true;
	    		}
	    	}
		} catch (Exception e) {
			e.printStackTrace();
	    }
    	return false;
	}
	
	public static boolean isLandscapeImage(Bitmap img) {
		return (img.getWidth() > img.getHeight());
	}
}
