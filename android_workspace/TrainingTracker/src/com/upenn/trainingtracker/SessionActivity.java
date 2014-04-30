package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.upenn.trainingtracker.customviews.SessionCategoryWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
	private List<String> catKeys = new ArrayList<String>();
	private Map<String, SessionCategoryWidget> catKeyToWidget;
	private int dogID;
	private String userName;
	private LinearLayout binLayout;
	private static final int EDIT_PLAN_RESULT = 100;
	
	private String catKeyBeingEdited;
	private int indexOfCatKeyBeingEdited;
	private SessionCategoryWidget widgetBeingEdited;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.session_layout);
		this.catKeyToWidget = new HashMap<String, SessionCategoryWidget>();
		
		Bundle extras = this.getIntent().getExtras();
		String[] catKeysArray = extras.getStringArray("categoryKeys");
		for (String catKey : catKeysArray)
		{
			catKeys.add(catKey);
		}
		this.dogID = extras.getInt("dogID");
		SharedPreferences preferences = this.getSharedPreferences(MainActivity.USER_PREFS, 0);
		this.userName = preferences.getString(MainActivity.USER_NAME_KEY, "");
		
		binLayout = (LinearLayout) this.findViewById(R.id.bin);
		
		
		for (String cat : catKeys)
		{
			SessionCategoryWidget widget = this.createWidgetByCatKey(cat);
			binLayout.addView(widget);
			this.catKeyToWidget.put(cat, widget);
		}
	}
	private SessionCategoryWidget createWidgetByCatKey(String catKey)
	{
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TrainingInfoTether tether = TrainingInfoTether.getInstance();

		SessionCategoryWidget widget = (SessionCategoryWidget) inflater.inflate(R.layout.session_category_widget, null);
		Map<String,String> planMap = tether.getPlanByCategoryKey(catKey, dogID, this);
		widget.initializeView(catKey, planMap, this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 10, 0, 10);
		widget.setLayoutParams(params);
		return widget;
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
				for (String catKey : catKeys)
				{
					SessionCategoryWidget widget = catKeyToWidget.get(catKey);
					if (widget.isStarted())
					{
						SessionActivity.this.recordCategory(catKey);
					}
				}
				SessionActivity.this.finish();
				SessionActivity.this.launchCheckOutActivity();
			}
    	});
    	builder.setNegativeButton("No", null);
    	builder.create().show();		
	}
	private void launchCheckOutActivity()
	{
		String[] catKeys = new String[this.catKeys.size()];
		for (int index = 0; index < this.catKeys.size(); ++index)
		{
			catKeys[index] = this.catKeys.get(index);
		}
		Intent intent = new Intent(this, CheckOutActivity.class);
		intent.putExtra("categoryKeys", catKeys);
		intent.putExtra("dogID", this.dogID);
		this.startActivity(intent);
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
			message = "Would you like to submit this session?";
		}
		return message;
	}
	public void recordCategory(String catKey)
	{
		SessionCategoryWidget widget = catKeyToWidget.get(catKey);
		this.recordWidget(catKey, widget);
	}
	public void recordWidget(String catKey, SessionCategoryWidget widget)
	{
		String dateString = Keys.getCurrentDateString();
		TrainingInfoTether tether = TrainingInfoTether.getInstance();
	    tether.addEntry(dateString, catKey, dogID, userName, widget.getResultSequence(), this);
	}
	/*
	 * Called from the widget when the user selects edit icon
	 */
	public void editPlan(String catKey)
	{
		SessionCategoryWidget widget = this.catKeyToWidget.get(catKey);
		this.catKeyBeingEdited = catKey;
		this.widgetBeingEdited = widget;
		this.indexOfCatKeyBeingEdited = this.catKeys.indexOf(catKey);
		
		this.removeWidget(catKey);
		
		if (this.widgetBeingEdited.isStarted())
		{
			this.recordWidget(this.catKeyBeingEdited, this.widgetBeingEdited);
		}
		
		// Launch the PlanActivity
		Intent intent = new Intent(this, CheckOutActivity.class);
		String[] categories = {catKey};
		intent.putExtra("categoryKeys", categories);
		intent.putExtra("dogID", this.dogID);
		this.startActivityForResult(intent, SessionActivity.EDIT_PLAN_RESULT);
	}
	public void removeWidget(String catKey)
	{
		SessionCategoryWidget widget = this.catKeyToWidget.get(catKey);
		this.binLayout.removeView(widget);
		this.catKeyToWidget.remove(catKey);
		this.catKeys.remove(catKey);
	}
	public void addNewWidget(String catKey)
	{
		SessionCategoryWidget widget = this.createWidgetByCatKey(this.catKeyBeingEdited);
		this.catKeyToWidget.put(this.catKeyBeingEdited, widget);
		this.catKeys.add(this.indexOfCatKeyBeingEdited, catKey);
		this.refreshBinLayout();
	}
	public void refreshBinLayout()
	{
		this.binLayout.removeAllViews();
		for (String catKey : this.catKeys)
		{
			SessionCategoryWidget widget = this.catKeyToWidget.get(catKey);
			this.binLayout.addView(widget);
		}
		this.binLayout.invalidate();
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.i("TAG","Result deliverde");
		switch (requestCode)
		{
		case SessionActivity.EDIT_PLAN_RESULT:
			// Add the new widget if a new plan was added
			if (resultCode == RESULT_OK)
			{
				this.addNewWidget(this.catKeyBeingEdited);
			}
			this.catKeyBeingEdited = null;
			this.widgetBeingEdited = null;
			break;
		}
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
