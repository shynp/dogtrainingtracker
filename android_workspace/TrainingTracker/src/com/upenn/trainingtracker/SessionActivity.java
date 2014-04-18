package com.upenn.trainingtracker;

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
		
		LinearLayout binLayout = (LinearLayout) this.findViewById(R.id.bin);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for (String cat : catKeys)
		{
			LinearLayout view = (LinearLayout) inflater.inflate(R.layout.session_category_widget, null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 10, 0, 10);
			view.setLayoutParams(params);
			binLayout.addView(view);
		}
	}
	
}
