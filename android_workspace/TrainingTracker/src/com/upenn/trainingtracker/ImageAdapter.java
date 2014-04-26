package com.upenn.trainingtracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ImageAdapter extends ArrayAdapter<Integer>
{
	private Integer[] images;

	public ImageAdapter(Context context, Integer[] images) {
	    super(context, android.R.layout.simple_spinner_item, images);
	    this.images = images;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
	    return getView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		TextView textView = (TextView) super.getView(position, convertView, parent);
		textView.setText("");
		Drawable imageDrawable = this.getContext().getResources().getDrawable(images[position]);
		textView.setCompoundDrawablesWithIntrinsicBounds(imageDrawable, null, null, null);
		return textView;
	}


}
