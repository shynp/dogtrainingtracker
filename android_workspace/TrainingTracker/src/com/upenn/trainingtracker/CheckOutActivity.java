package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.upenn.trainingtracker.customviews.AutoCategorySelector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class CheckOutActivity extends Activity
{
	Map<String, ArrayList<PlanEntry>> categoryToPlanEntries;
	Map<String, View> categoryToView;
	private String currentCategory;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.check_out_layout);
		Bundle extras = this.getIntent().getExtras();
		String[] categories = extras.getStringArray("categories");
		this.currentCategory = categories[0];
		this.initializeLayout(categories);
	}
	private void initializeLayout(String[] categories)
	{
		Spinner spinner = (Spinner) this.findViewById(R.id.categorySelectorID);
		
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				String category = parent.getItemAtPosition(position).toString();
				CheckOutActivity.this.switchToViewByCategory(category);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		TrainingReader reader = TrainingReader.getInstance(this);
		this.categoryToPlanEntries = new HashMap<String, ArrayList<PlanEntry>>();
		this.categoryToView = new HashMap<String, View>();
				
		// Initialize category to entries structure and category to view structure
		for (String category : categories)
		{
			ArrayList<PlanEntry> entries = reader.getViewCompositionByCategory(category);
			this.categoryToPlanEntries.put(category, entries);
			this.categoryToView.put(category, this.getViewForCategory(category));
		}
		// Set view to current view
		//ScrollView parentLayout = (ScrollView) this.findViewById(R.id.checkOutScrollBin);
		//parentLayout.addView(this.categoryToView.get(this.currentCategory));
		this.switchToViewByCategory(this.currentCategory);
	}
	
	private void switchToViewByCategory(String category)
	{
		ScrollView parentLayout = (ScrollView) this.findViewById(R.id.checkOutScrollBin);
		parentLayout.removeAllViews();
		View view = this.categoryToView.get(category);
		parentLayout.addView(view);
	}
	
	
	
	/*
	 * Methods for rendering the views are below
	 */
	private View getViewForCategory(String category)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout viewBin = (LinearLayout) inflater.inflate(R.layout.check_out_view_bin, null);
		LinearLayout leftBin = (LinearLayout) viewBin.findViewById(R.id.leftBin);
		LinearLayout rightBin = (LinearLayout) viewBin.findViewById(R.id.rightBin);
		for (PlanEntry entry: this.categoryToPlanEntries.get(category))
		{
			switch (entry.getType())
			{
			case CHECKBOX: 
				LinearLayout canvasLayout = (LinearLayout) this.getCheckBoxFromEntry(entry);
				View checkBox = canvasLayout.findViewById(R.id.optionsCheckWidget);
				View checkText = canvasLayout.findViewById(R.id.optionsCheckWidgetText);
				canvasLayout.removeAllViews();
				leftBin.addView(checkText);
				rightBin.addView(checkBox);
			break;
			case OPTIONS: 
				LinearLayout canvasLayout2 = this.getSpinnerFromEntry(entry);
				View spinner = canvasLayout2.findViewById(R.id.optionsSpinnerID);
				View text = canvasLayout2.findViewById(R.id.optionsTextID);
				canvasLayout2.removeAllViews();
				leftBin.addView(text);
				rightBin.addView(spinner);
			break;
			case IMAGE_OPTIONS:// view = this.getImageSpinnerFromEntry(entry);
			break;
			}
		}
		return viewBin;
	}
	private LinearLayout getCheckBoxFromEntry(PlanEntry entry)
	{
		Log.i("TAG","Adding checkbox");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout checkLayout = (LinearLayout) inflater.inflate(R.layout.options_check_widget, null);
		
		CheckBox check = (CheckBox) checkLayout.findViewById(R.id.optionsCheckWidget);
		//check.setText(entry.getName());
		
		TextView text = (TextView) checkLayout.findViewById(R.id.optionsCheckWidgetText);
		text.setText(entry.getName());
		
		
		return checkLayout;
	}
	private LinearLayout getSpinnerFromEntry(PlanEntry entry)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.options_widget, null);
		
		TextView text = (TextView) layout.findViewById(R.id.optionsTextID);
		text.setText(entry.getName());
		
		Spinner spinner = (Spinner) layout.findViewById(R.id.optionsSpinnerID);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, entry.getOptions());
		spinner.setAdapter(spinnerArrayAdapter);
		
		return layout;
	}
	private Spinner getImageSpinnerFromEntry(PlanEntry entry)
	{
		return null; //TODO: IMPLEMENT THIS
	}

	
}
