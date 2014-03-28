package com.upenn.trainingtracker.customviews;

import java.util.List;

import com.upenn.trainingtracker.TrainingReader;
import com.upenn.trainingtracker.TrainingSelectorActivity;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class AutoCategorySelector extends AutoCompleteTextView
{
	private TrainingSelectorActivity parent;
	
	public AutoCategorySelector(Context context)
	{
		super(context);
	}
	public AutoCategorySelector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public AutoCategorySelector(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	public void setParentAndInitialize(TrainingSelectorActivity activity)
	{
		this.parent = activity;
		this.init();
		this.attachListener();
	}
	public void init()
	{
		Log.i("TAG","Settin adapter");
		TrainingReader reader = TrainingReader.getInstance(this.parent);
		List<String> allCategories = reader.getAllCategories();
		Log.i("TAG","Num categories: " + allCategories.size());
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent,
                android.R.layout.simple_dropdown_item_1line, allCategories);
    	this.setAdapter(adapter);
	}
	public void attachListener()
	{
		this.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) 
			{
				String category = (String) arg0.getItemAtPosition(position);
				AutoCategorySelector.this.parent.addNewCategory(category);
				AutoCategorySelector.this.setText("");
			}
		});
	}
	
}
