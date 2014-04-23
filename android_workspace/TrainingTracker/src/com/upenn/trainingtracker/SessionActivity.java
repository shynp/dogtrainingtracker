package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.upenn.trainingtracker.customviews.SessionCategoryWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class SessionActivity extends Activity
{
	private String[] catKeys;
	private Map<String, SessionCategoryWidget> catKeyToWidget;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.session_layout);
		this.catKeyToWidget = new HashMap<String, SessionCategoryWidget>();
		
		Bundle extras = this.getIntent().getExtras();
		this.catKeys = extras.getStringArray("categoryKeys");
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
			params.setMargins(0, 10, 0, 10);
			widget.setLayoutParams(params);
			binLayout.addView(widget);
			this.catKeyToWidget.put(cat, widget);
		}
	}

	public void collapseAllWidgets()
	{
		for (String catKey : this.catKeys)
		{
			SessionCategoryWidget widget = this.catKeyToWidget.get(catKey);
			widget.collapseView();
		}
	}
	public void recordAllCategories(final View view)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(Html.fromHtml(this.getRecordMessage()));
    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
    	{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				
			}
    	});
    	builder.setNegativeButton("No", null);
    	builder.create().show();		
	}
	public String getRecordMessage()
	{
		List<String> incomplete = new ArrayList<String>();
		List<String> notStarted = new ArrayList<String>();
		for (String catKey : this.catKeys)
		{
			SessionCategoryWidget widget = this.catKeyToWidget.get(catKey);
			if (!widget.isStarted())
			{
				notStarted.add(catKey);
			}
			else if (!widget.isCompleted())
			{
				incomplete.add(catKey);
			}
		}
		String message = "";
		TrainingReader reader = TrainingReader.getInstance(this);
		if (!incomplete.isEmpty())
		{
			message = "<b>If you submit now the following categories will be tagged as aborted:</b> <br>";
			for (String catKey : incomplete)
			{
				String category = reader.catKeyToCategory(catKey);
				message += category + " <br>";
			}
			message += "<br>";
		}
		if (!notStarted.isEmpty())
		{
			message += "<b>If you submit now the following categories will be deleted</b>: <br>";
			for (String catKey : notStarted)
			{
				String category = reader.catKeyToCategory(catKey);
				message += category + " <br>";
			}
			message += "<br>";
		}
		if (!notStarted.isEmpty() || !incomplete.isEmpty())
		{
			message += "Are you sure you want to continue?";
		}
		else
		{
			message = "All categories are complete!";
		}
		return message;
	}
	public void recordCategory(String catKey)
	{
		
	}
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	 switch (item.getItemId())
         {
         case R.id.collapseAllID:
        	 this.collapseAllWidgets();
         default:
             return super.onOptionsItemSelected(item);
         }
    }
     // Initiating Menu XML file (menu.xml)
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu)
	 {
	     MenuInflater menuInflater = getMenuInflater();
	     menuInflater.inflate(R.menu.session_menu, menu);
	     return true;
	 }
	
}
