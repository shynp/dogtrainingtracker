package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.upenn.trainingtracker.CheckOutActivity.MyGestureDetector;
import com.upenn.trainingtracker.customviews.FlowLayout;
import com.upenn.trainingtracker.customviews.SessionCategoryWidget;
import com.upenn.trainingtracker.customviews.TagButton;

import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends Activity
{
	private List<String> parentCats;
	private List<String> subCats;
	private List<String> userNames;
	private List<String> userFullNames;
	
	private int dogID;
	private FlowLayout filterBin;
	private ArrayAdapter<String> autoAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.history_layout);
		this.filterBin = (FlowLayout) this.findViewById(R.id.filterBinID);
		
		Bundle extras = this.getIntent().getExtras();
		this.dogID = extras.getInt("dogID");
		
		this.initializeFilterCriteria();
		
		ListView list = (ListView) this.findViewById(R.id.list);
		HistoryTether tether = HistoryTether.getInstance();
		try
		{
			HistoryAdapter adapter = tether.getTrainingSessionAdapterForDog(dogID, this);
			list.setAdapter(adapter);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	public void initializeFilterCriteria()
	{
		TrainingReader reader = TrainingReader.getInstance(this);
		this.parentCats = reader.getParentCategories();
		this.subCats = reader.getAllCategories();
		UserTether tether = UserTether.getInstance();
		this.userNames = tether.getUserNames(this);
		this.userFullNames = tether.getUserFullNames(this);
		
		List<String> autoList = new ArrayList<String>();
		autoList.addAll(this.parentCats);
		autoList.addAll(this.subCats);
		autoList.addAll(this.userNames);
		autoList.addAll(this.userFullNames);
		
		final AutoCompleteTextView textView = (AutoCompleteTextView) this.findViewById(R.id.historyFilterID);
		autoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, autoList);
		textView.setAdapter(autoAdapter);
		textView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) 
			{
				String selected = (String) HistoryActivity.this.autoAdapter.getItem(position);
				ArrayList<String> tags = new ArrayList<String>();
				for (int i = 0; i < filterBin.getChildCount(); ++i)
				{
					Button tagButton = (Button)filterBin.getChildAt(i);
					tags.add(tagButton.getText().toString());
				}
				// Check for duplicates
				if (tags.contains(selected))
				{
					textView.setText("");
					return;
				}
				TagButton tag = new TagButton(HistoryActivity.this, selected);
				HistoryActivity.this.filterBin.addView(tag);
				textView.setText("");
			}
		});
	}
	
}
