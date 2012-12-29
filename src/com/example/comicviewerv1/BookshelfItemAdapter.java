package com.example.comicviewerv1;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookshelfItemAdapter extends ArrayAdapter<BookshelfItem> 
{
	private Context mContext;
    private int mResource;
    private ArrayList<BookshelfItem> mList;
    private LayoutInflater mInflater;

    public BookshelfItemAdapter(Context context, int layoutResource, ArrayList<BookshelfItem> objects) 
    {
    	super(context, layoutResource, objects);
    	this.mContext = context;
        this.mResource = layoutResource;
        this.mList = objects;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
    	BookshelfItem bookItem = mList.get(position);
 
        if(convertView == null)
        {
            convertView = mInflater.inflate(mResource, null);
        }
        
        if (bookItem != null) {
        	ImageView ivThumbnail = (ImageView) convertView.findViewById(R.id.book_item_row_thumbnail);
        	TextView tvTitle = (TextView) convertView.findViewById(R.id.book_item_row_title);
        	TextView tvDesc = (TextView) convertView.findViewById(R.id.book_item_row_desc);
        	
        	tvTitle.setText(bookItem.getTitle());
        	tvDesc.setText(bookItem.getBody());
        	ivThumbnail.setImageResource(R.drawable.ic_launcher);
        }
        
        return convertView;
    }
}
