package com.example.comicviewerv1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class ComicImageView extends View {
	
	
	private static final float PAGE_FEED_THRESHOLD = (float)7.0; // 10% slide will make next page
	private Bitmap image = null;
	private int canvasWidth = 0;
	private int hShift = 0;  // horizontal shift bias by scroll
	
	
	
	public ComicImageView(Context context) {
		super(context);
	}
	
	public void setBitmap(Bitmap temp) {
		this.image = temp;
	}
	

	/**
	 *  return 1 if shift exceeds threshold of right page skip
	 *  return -1 if shift exceeds threshold of left page skip
	 *  otherwise 0
	 */	
	public int finishMove() {
		int res = 0;
		
		int threshold = (int) ((float)this.canvasWidth * PAGE_FEED_THRESHOLD / 100);
		if (this.hShift > 0 && (this.hShift > threshold)) {
			res = 1; // move right
			
		} else if (this.hShift < 0 && (-1*this.hShift > threshold)) {
			res = -1; // move left
		}
		
		this.hShift = 0;
		return res;
		
		
	}
	

	public void move(int hShift) {
		if (this.canvasWidth <= 0) 
			return;
		
		this.hShift += hShift;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (image == null) return;
		
		this.canvasWidth = canvas.getWidth();
		
		//  width/height
		float imageRatio = (float)image.getWidth() / (float)image.getHeight();
		float canvasRatio = (float)canvas.getWidth() / (float)canvas.getHeight();
		Rect src = new Rect(0, 0, image.getWidth(), image.getHeight());

		float zoomRatio = 0;
		int left, top, right, bottom;
		if (imageRatio > canvasRatio) {
			// fit in width
			zoomRatio = (float)canvas.getWidth() / (float)image.getWidth();
			int adjustedHeight = (int)((float)image.getHeight() * zoomRatio);
			int center = canvas.getHeight()/2;
			left = 0;
			top = center-adjustedHeight/2;
			right = canvas.getWidth();
			bottom = center+adjustedHeight/2;			
		} else {
			// fit in height
			zoomRatio = (float)canvas.getHeight() / (float)image.getHeight();
			int adjustedWidth = (int)((float)image.getWidth() * zoomRatio);
			int center = canvas.getWidth()/2;
			left = center-adjustedWidth/2;
			top = 0;
			right = center+adjustedWidth/2;
			bottom = canvas.getHeight();
		}
		

		Rect dst = new Rect(left-hShift, top, right-hShift, bottom);
		canvas.drawBitmap(image,  src,  dst, null);	

	}
}
