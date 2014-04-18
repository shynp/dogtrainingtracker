package com.upenn.trainingtracker;

import java.util.Map;

import com.upenn.trainingtracker.customviews.SessionCategoryWidget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class SessionActivity extends Activity
{
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.session_layout);
		
		Bundle extras = this.getIntent().getExtras();
		String[] catKeys = extras.getStringArray("categoryKeys");
		int dogID = extras.getInt("dogID");
		
		LinearLayout binLayout = (LinearLayout) this.findViewById(R.id.bin);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TrainingInfoTether tether = TrainingInfoTether.getInstance();
		
		for (String cat : catKeys)
		{
			SessionCategoryWidget widget = (SessionCategoryWidget) inflater.inflate(R.layout.session_category_widget, null);
			Map<String,String> planMap = tether.getPlanByCategoryKey(cat, dogID, this);
			widget.initializeView(cat, planMap);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(20, 10, 20, 10);
			widget.setLayoutParams(params);
			binLayout.addView(widget);
		}
		
	}
	
}
