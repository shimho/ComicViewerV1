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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
	private ReadingBookInfo mCurrentBook = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_fullscreen);
		loadPreferences();
		
		
		
		mComicImageView = new ComicImageView(this);
		LinearLayout fullLayout = (LinearLayout)findViewById(R.id.fullscreen_content);
		fullLayout.addView(mComicImageView);
		mGestureDetector =  new GestureDetector(getApplicationContext(), this);
		
		FrameLayout controlLayout = (FrameLayout)findViewById(R.id.fullscreen_controls);
		controlLayout.setVisibility(View.INVISIBLE);
	}
	
	public void openLibrary(View v) {
		Intent intent = new Intent(FullscreenActivity.this, BookshelfActivity.class);
		startActivityForResult(intent, ACTIVITY_BOOK_SELECTION);
	}
	
	public void toggleReadDirection(View v) {
		mAppConfig.setFlagLeftToRight(!mAppConfig.getFlagLeftToRight());
		updateControlViewInfo();
		openPage(ComicImageView.SLIDE_NO_EFFECT);
	}
	
	public void changeLibraryPath(View v) {
		// TODO
	}
	
	public void leftBook(View v) {
		if (mAppConfig.getFlagLeftToRight()) 
			prevBook(v);
		else
			nextBook(v);
	}
	
	public void rightBook(View v) {
		if (mAppConfig.getFlagLeftToRight()) 
			nextBook(v);
		else
			prevBook(v);
	}
	
	public void prevBook(View v) {
		
		if (mCurrentBook.prevBook()) {
			openPage(ComicImageView.SLIDE_NO_EFFECT);
		} else {
			Toast.makeText(getApplicationContext(), R.string.firstbook,
					Toast.LENGTH_SHORT).show();
		}
		updateControlViewInfo();
	}
	
	public void nextBook(View v) {
		if (mCurrentBook.nextBook()) {
			openPage(ComicImageView.SLIDE_NO_EFFECT);
		} else {
			Toast.makeText(getApplicationContext(), R.string.lastbook,
					Toast.LENGTH_SHORT).show();
		}
		updateControlViewInfo();
	}
	
	public void left20Page(View v) {
		if (mAppConfig.getFlagLeftToRight()) 
			prev20Page(v);
		else
			next20Page(v);
	}
	
	public void right20Page(View v) {
		if (mAppConfig.getFlagLeftToRight()) 
			next20Page(v);
		else
			prev20Page(v);
	}
	
	public void prev20Page(View v) {
		if (mCurrentBook.prevPage(20)) {
			openPage(ComicImageView.SLIDE_NO_EFFECT);
		} else {
			Toast.makeText(getApplicationContext(), R.string.firstpage,
						Toast.LENGTH_SHORT).show();
		}
		updateControlViewInfo();
	}
	
	public void next20Page(View v) {
		if (mCurrentBook.nextPage(20)) {
			openPage(ComicImageView.SLIDE_NO_EFFECT);
		} else {
			Toast.makeText(getApplicationContext(), R.string.lastpage,
						Toast.LENGTH_SHORT).show();
		}
		updateControlViewInfo();

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
		if (mCurrentBook == null) {
			// no book loaded yet
			return false;
		}
		
		int pageSkip = mComicImageView.finishMove();
		if ((pageSkip > 0 && mAppConfig.getFlagLeftToRight()) ||
			(pageSkip < 0 && !mAppConfig.getFlagLeftToRight())) {
			nextPage(1);
			mComicImageView.finishMove();
		} else if ((pageSkip < 0 && mAppConfig.getFlagLeftToRight()) ||
				   (pageSkip > 0 && !mAppConfig.getFlagLeftToRight())) {
			prevPage(1);
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
    	
    	updateControlViewInfo();
    	
    	int pageButtonVisibility = View.VISIBLE;
    	if (mCurrentBook == null) {
    		// hide some bottuns, not applicable when no book is loaded
    		pageButtonVisibility = View.INVISIBLE;
    	}
    		
    	((Button)findViewById(R.id.button_next_20page)).setVisibility(pageButtonVisibility);
    	((Button)findViewById(R.id.button_prev_20page)).setVisibility(pageButtonVisibility);
    	((Button)findViewById(R.id.button_next_book)).setVisibility(pageButtonVisibility);
    	((Button)findViewById(R.id.button_prev_book)).setVisibility(pageButtonVisibility);
	
    	FrameLayout controlLayout = (FrameLayout)findViewById(R.id.fullscreen_controls);
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
    	
    	if (mCurrentBook != null) {
    		// remove library path from book file path
    		String bookTitle;
    		bookTitle = mCurrentBook.getBookTitle();
    		
    		TextView tvTitle = (TextView)findViewById(R.id.text_book_title);
    		tvTitle.setText(bookTitle);
        	
        	TextView tvPage = (TextView)findViewById(R.id.text_page_info);
        	
        	String pageInfo = "";
        	if (mCurrentBook.getTotalBooks() > 1) {
        		pageInfo += mCurrentBook.getCurrentBookIndex()+1 + "/"+ mCurrentBook.getTotalBooks() + "권  ";
        	}
        	pageInfo += mCurrentBook.getCurrentPageIndex()+1 + "/" + mCurrentBook.getTotalPages() + "페이지";
        	tvPage.setText(pageInfo);

    	}
    	
    	TextView tvLibPath = (TextView)findViewById(R.id.text_library_root);
    	tvLibPath.setText(mAppConfig.getLibraryRootPath());
    	
    	TextView tvDirection = (TextView)findViewById(R.id.text_read_direction);
    	if (mAppConfig.getFlagLeftToRight()) {
    		tvDirection.setText(R.string.label_l2r);
    	} else {
    		tvDirection.setText(R.string.label_r2l);
    	}

    }
	
	final int BUFFER = 2048;
	final int MAX_JPG_SIZE = 1024*1024;
	private boolean loadBook(String bookFilePath) {
		hideControlView();
		mCurrentBook = new ReadingBookInfo(bookFilePath);
		if (!mCurrentBook.load(0, 0)) {
			return false;
		}
		return openPage(ComicImageView.SLIDE_NO_EFFECT);
	}
	
	/**
	 * 
	 * @param pageIndex
	 * @param slideEffect : 0 (no slide effect), 1 (slide from right), -1 (slide from left)
	 * @return
	 */
	public boolean openPage(int slideEffect) {
		Bitmap curImg = null, nextImg = null;
		String currentPageFile, nextPageFile;
        currentPageFile = mCurrentBook.getPageFile();
        nextPageFile = mCurrentBook.getNextPageFile();
        
		try {
	        FileInputStream fis = new FileInputStream(mCurrentBook.getBookPath());
	        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	        ZipEntry entry;
	        while((entry = zis.getNextEntry()) != null) {
	        	if (entry.getName().compareTo(currentPageFile) == 0) {
	        		curImg = readBitmapFromZIS(zis);
	        	} else if (entry.getName().compareTo(nextPageFile) == 0) {
	        		nextImg = readBitmapFromZIS(zis);
	        	}
	        	
	        	if (curImg != null) {
	        		if (ComicUtil.isLandscapeImage(curImg)) {
	        			nextImg = null;
		        	   	break;
		        	} else if (nextPageFile.isEmpty()) {
		        		
		        		break;
		        	} else if (nextImg != null) {
		        		if (ComicUtil.isLandscapeImage(nextImg)) {
		        			nextImg = null;
		        		} 
		        		break;
		        	}
	        	}
	        }
	        
	        zis.close();
		} catch(Exception e) {
			e.printStackTrace();
			return false;
	    }
		
		Bitmap finalImg = null;
		if (curImg != null && nextImg == null) {
			mCurrentBook.setShowTwoPages(false);
			finalImg = curImg;
			Log.d("HSHIM", "One page view : " + currentPageFile);
			
		} else if (curImg != null && nextImg != null) {
			
			mCurrentBook.setShowTwoPages(true);
			Log.d("HSHIM", "Two pages view : " + currentPageFile + "," + nextPageFile);
			int minWidth, minHeight;
			minWidth = Math.min(curImg.getWidth(), nextImg.getWidth());
			minHeight = Math.min(curImg.getHeight(), nextImg.getHeight());
			
			curImg = Bitmap.createScaledBitmap(curImg, minWidth, minHeight, false);
			nextImg = Bitmap.createScaledBitmap(nextImg, minWidth, minHeight, false);
			finalImg = Bitmap.createScaledBitmap(curImg, minWidth*2,minHeight, true);
			
			Paint p = new Paint();
			p.setDither(true);
			p.setFlags(Paint.ANTI_ALIAS_FLAG);

			Canvas c = new Canvas(finalImg);
			c.drawColor(0xffffffff);
			if (mAppConfig.getFlagLeftToRight()) {
				c.drawBitmap(curImg, 0, 0, p);
				c.drawBitmap(nextImg, curImg.getWidth(), 0, p);
			} else {
				c.drawBitmap(nextImg, 0, 0, p);
				c.drawBitmap(curImg, curImg.getWidth(), 0, p);
			}
		}
		
		if (finalImg != null) {
			mComicImageView.setBitmap(finalImg, slideEffect);
		   	mComicImageView.invalidate();
		   	return true;
		}
		return false;
	}
	
	
	private Bitmap readBitmapFromZIS(ZipInputStream zis) {
		int jpgByte = 0;
    	byte jpgData[] = new byte[MAX_JPG_SIZE];
    	
    	int bufReadByte;
    	byte buf[] = new byte[BUFFER];
		try {
	        while ((bufReadByte = zis.read(buf, 0, BUFFER)) != -1) {
	        	System.arraycopy(buf, 0, jpgData, jpgByte, bufReadByte);
	        	jpgByte += bufReadByte;
	        }
		} catch(Exception e) {
			e.printStackTrace();
			return null;
	    }
        
        Bitmap tmpImg = BitmapFactory.decodeByteArray(jpgData, 0, jpgByte);
        return tmpImg;
	}
	
	public boolean nextPage(int n) {
		
		if (mCurrentBook.nextPage(n)) {
			openPage(ComicImageView.SLIDE_FROM_RIGHT);
		} else {
			Toast.makeText(getApplicationContext(), 
						R.string.lastpage, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public boolean prevPage(int n) {
		if (mCurrentBook.prevPage(n)) {
			openPage(ComicImageView.SLIDE_FROM_LEFT);
		} else {
			Toast.makeText(getApplicationContext(), 
						R.string.firstpage, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void loadPreferences() {
		SharedPreferences prefs = getSharedPreferences(CONFIG_NAME, MODE_PRIVATE);
		
		String defaultRoot = Environment.getExternalStorageDirectory().getPath() + "/Comics/";
		String libraryRootPath = prefs.getString(KEY_LIBRARY_ROOT_PATH, defaultRoot);
		
		
		// path sanity check
		File tmpf = new File(libraryRootPath);
		if (!tmpf.exists()) {
			tmpf.mkdir();
			
		}
		
		boolean flagLeftToRight = prefs.getBoolean(KEY_LEFT_TO_RIGHT, true);
		
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
