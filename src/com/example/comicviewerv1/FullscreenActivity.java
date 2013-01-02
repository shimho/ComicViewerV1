package com.example.comicviewerv1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import com.example.comicviewerv1.util.SystemUiHider;
import com.example.comicviewerv1.BookshelfActivity;
import net.sf.jazzlib.*;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity implements OnGestureListener {


	private static final int ACTIVITY_BOOK_SELECTION = 15;
	
	// app setting constants
	private static final String CONFIG_NAME = "AppSetting";
	private static final String KEY_LIBRARY_ROOT_PATH = "library_root_path";
	private static final String KEY_LEFT_TO_RIGHT = "flag_left_to_right";


	private ComicImageView mComicImageView = null;
	private AppConfig mAppConfig = null;
	private GestureDetector mGestureDetector = null;;
	private String mCurrentBookFilePath;
	private ArrayList<BookPage> mBookPages;
	private int mCurrentPageIndex = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_fullscreen);
		loadPreferences();
		
		mComicImageView = new ComicImageView(this);
		LinearLayout fullLayout = (LinearLayout)findViewById(R.id.fullscreen_content);
		fullLayout.addView(mComicImageView);
		mGestureDetector =  new GestureDetector(getApplicationContext(), this);
		mBookPages = new ArrayList<BookPage>();
		
		FrameLayout controlLayout = (FrameLayout)findViewById(R.id.fullscreen_controls);
		controlLayout.setVisibility(View.INVISIBLE);
		 
		
	
		Button btnOpenBookShelf = (Button)findViewById(R.id.button_open_library);
		btnOpenBookShelf.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(FullscreenActivity.this, BookshelfActivity.class);
					startActivityForResult(intent, ACTIVITY_BOOK_SELECTION);
			}
		});
		
		Button btnToggleDirection = (Button)findViewById(R.id.button_read_direction);
		btnToggleDirection.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				mAppConfig.setFlagLeftToRight(!mAppConfig.getFlagLeftToRight());
				updateControlViewInfo();
			}
		});

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(0, resultCode, null);
		
		if (ACTIVITY_BOOK_SELECTION == requestCode) {
			// book was just selected
			if (RESULT_OK == resultCode) {
				String bookFilePath = data.getStringExtra("bookFilePath");
				Toast.makeText(
		                getApplicationContext(),
		                bookFilePath,
		                Toast.LENGTH_SHORT
		            ).show();
				
				loadBook(bookFilePath);
			}
			
		}
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent me) {
		
		switch (me.getAction()) {
		case MotionEvent.ACTION_UP:
			onActionUp();
		}

        return mGestureDetector.onTouchEvent(me);
    }
	
	private boolean onActionUp() {
		if (mBookPages.size() == 0) {
			// no book loaded yet
			return false;
		}
		
		int pageSkip = mComicImageView.finishMove();
		if (pageSkip > 0) { // move right
			nextPage();
			mComicImageView.finishMove();
		} else if (pageSkip < 0) { // move left
			prevPage();
			mComicImageView.finishMove();
		}
		mComicImageView.invalidate();
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
    }
	
    @Override
    public boolean onDown(MotionEvent e) {
    	return false;
    }
   
    @Override
    public void onLongPress(MotionEvent e) {
    }
   
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    	
    	// if control box is visible, hide it first
    	FrameLayout controlLayout = (FrameLayout)findViewById(R.id.fullscreen_controls);
    	if (controlLayout.getVisibility() == View.VISIBLE) {
    		controlLayout.setVisibility(View.INVISIBLE);
    	}
    	this.mComicImageView.move((int)distanceX);
    	this.mComicImageView.invalidate();
    	
        return false;
    }
   
    @Override
    public void onShowPress(MotionEvent e) {
    }    
   
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
 
    	toggleControlView();
        return true;
    }
    
    private void showControlView() {
    	FrameLayout controlLayout = (FrameLayout)findViewById(R.id.fullscreen_controls);
    	updateControlViewInfo();
    	controlLayout.setVisibility(View.VISIBLE); 
    }
    
    private void hideControlView() {
    	FrameLayout controlLayout = (FrameLayout)findViewById(R.id.fullscreen_controls);
    	controlLayout.setVisibility(View.INVISIBLE);
    }
    
    private void toggleControlView() {
    	FrameLayout controlLayout = (FrameLayout)findViewById(R.id.fullscreen_controls);
    	if (controlLayout.getVisibility() == View.INVISIBLE) {
    		showControlView();
    	} else {
    		hideControlView();
    	}
    }
    
    private void updateControlViewInfo() {
    	
    	if (mBookPages.size() > 0) {
    		// remove library path from book file path
    		String bookTitle;
    		bookTitle = mCurrentBookFilePath.substring(mAppConfig.getLibraryRootPath().length());
    		
    		TextView tvTitle = (TextView)findViewById(R.id.text_book_title);
    		tvTitle.setText(bookTitle);
        	
        	TextView tvPage = (TextView)findViewById(R.id.text_page_info);
        	String pageInfo = "" + mCurrentPageIndex + "/" + mBookPages.size();
        	tvPage.setText(pageInfo);

    	}
    	
    	TextView tvLibPath = (TextView)findViewById(R.id.text_library_root);
    	tvLibPath.setText(mAppConfig.getLibraryRootPath());
    	
    	TextView tvDirection = (TextView)findViewById(R.id.text_read_direction);
    	if (mAppConfig.getFlagLeftToRight()) {
    		tvDirection.setText("Left->Right");
    	} else {
    		tvDirection.setText("Right->Left");
    	}

    }
	
	final int BUFFER = 2048;
	final int MAX_JPG_SIZE = 1024*1024;
	private boolean loadBook(String bookFilePath) {
		hideControlView();
		mBookPages.clear();
		mCurrentBookFilePath = bookFilePath;
		try {
			int zipOffset = 0;
	        FileInputStream fis = new FileInputStream(bookFilePath);
	        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	        ZipEntry entry;
	        while((entry = zis.getNextEntry()) != null) {
	        	String filename = entry.getName();
	            int zipSize = (int)entry.getSize();
	            
	            mBookPages.add(new BookPage(filename, zipOffset, zipSize));
	            zipOffset += zipSize;
	        }
	        zis.close();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
	    }
		
		Collections.sort(mBookPages, new BookPageComparator());
		return openPage(0, ComicImageView.SLIDE_NO_EFFECT);
	}
	
	/**
	 * 
	 * @param pageIndex
	 * @param slideEffect : 0 (no slide effect), 1 (slide from right), -1 (slide from left)
	 * @return
	 */
	public boolean openPage(int pageIndex, int slideEffect) {
		try {
	        FileInputStream fis = new FileInputStream(mCurrentBookFilePath);
	        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	        ZipEntry entry;
	        while((entry = zis.getNextEntry()) != null) {
	        	if (entry.getName().compareTo(mBookPages.get(pageIndex).getFilename()) != 0) {
	        		continue;
	        	}
	        	
	        	int jpgByte = 0;
	        	byte jpgData[] = new byte[MAX_JPG_SIZE];
	        	
	        	int bufReadByte;
	        	byte buf[] = new byte[BUFFER];
	            while ((bufReadByte = zis.read(buf, 0, BUFFER)) != -1) {
	            	System.arraycopy(buf, 0, jpgData, jpgByte, bufReadByte);
	            	jpgByte += bufReadByte;
	            }
	            
	            mCurrentPageIndex = pageIndex;
	            
	            Bitmap tmpImg = BitmapFactory.decodeByteArray(jpgData, 0, jpgByte);
		    	mComicImageView.setBitmap(tmpImg, slideEffect);
		    	mComicImageView.invalidate();
		    	return true;
	        }
	        
	        
		} catch(Exception e) {
			e.printStackTrace();
	    }
		return false;
	}
	
	public boolean nextPage() {
		if (mCurrentPageIndex < mBookPages.size()-1) {
			openPage(mCurrentPageIndex+1, ComicImageView.SLIDE_FROM_RIGHT);
		} else {
			Toast.makeText(getApplicationContext(), 
						"Last Page", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public boolean prevPage() {
		if (mCurrentPageIndex > 0) {
			openPage(mCurrentPageIndex-1, ComicImageView.SLIDE_FROM_LEFT);
		} else {
			Toast.makeText(getApplicationContext(), 
						"First Page", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void loadPreferences() {
		SharedPreferences prefs = getSharedPreferences(CONFIG_NAME, MODE_PRIVATE);
		
		String defaultRoot = Environment.getExternalStorageDirectory().getPath() + "/Comics/";
		String libraryRootPath = prefs.getString(KEY_LIBRARY_ROOT_PATH, defaultRoot);
		
		Log.d("HSHIM", "LibraryRoot="+libraryRootPath);
		// path sanity check
		File tmpf = new File(libraryRootPath);
		if (!tmpf.exists()) {
			tmpf.mkdir();
			Log.d("HSHIM", "LibraryRoot created.");
		}
		
		boolean flagLeftToRight = prefs.getBoolean(KEY_LEFT_TO_RIGHT, true);
		Log.d("HSHIM", "LeftToRight flag="+flagLeftToRight);
		
		mAppConfig = new AppConfig(libraryRootPath, flagLeftToRight);
		savePreferences();
	}
	
	private void savePreferences() {
		SharedPreferences prefs = getSharedPreferences(CONFIG_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(KEY_LIBRARY_ROOT_PATH, mAppConfig.getLibraryRootPath());
		editor.putBoolean(KEY_LEFT_TO_RIGHT, mAppConfig.getFlagLeftToRight());
		editor.commit();

	}

}
