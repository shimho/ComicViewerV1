package com.example.comicviewerv1;


public class AppConfig {
	private String libraryRootPath;
	private boolean flagLeftToRight;
		
	public AppConfig(String rootPath, boolean flagL2R) {
		this.libraryRootPath = rootPath;
		this.flagLeftToRight = flagL2R;
	}
	
	public String getLibraryRootPath() {
		return this.libraryRootPath;
	}
	
	public boolean getFlagLeftToRight() {
		return this.flagLeftToRight;
	}
	
	public void setLibraryRootPath(String path) {
		this.libraryRootPath = path;
	}
	
	public void setFlagLeftToRight(boolean flag) {
		this.flagLeftToRight = flag;
	}

}	
