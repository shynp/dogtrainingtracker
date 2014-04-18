package com.upenn.trainingtracker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.regex.Pattern;

import android.util.Log;
import android.view.View;

import com.upenn.trainingtracker.PlanEntry.Type;

public class TrainingSession 
{
	private String trainerUserName;
	private String dogName;

	private Calendar sessionDate;
	
	private String catKey;
	private String category;
	
	public boolean success;
	
	private String[] entryTitles;
	private String[] entryValues;
	
	private View view;
	
	public String getCategory()
	{
		return this.category;
	}
	public TrainingSession(String catKey, String sessionDate, String plan, String trialsResult, String trainerUserName, String dogName)
	{
		Log.i("TAG","b");
		this.catKey = catKey;
		Log.i("TAG","b");
		TrainingReader reader = TrainingReader.getInstance(null);
		Log.i("TAG","b");
		if (!reader.isInitialized())
		{
			throw new IllegalStateException("Cannot create TrainingSession object before TrainingReader has been initialized");
		}
		this.category = reader.catKeyToCategory(catKey);
		Log.i("TAG","b");
		this.initializeDate(sessionDate);
		Log.i("TAG","b");
		this.initializedPlan(plan);
		Log.i("TAG","b");
		this.trainerUserName = trainerUserName;
		Log.i("TAG","b");
		this.dogName = dogName;
	}
	public void setView(View view)
	{
		this.view = view;
	}
	public View getView()
	{
		return this.view;
	}
	private void initializedPlan(String plan)
	{
		String[] planParts = plan.split(Pattern.quote("||"));
		this.entryTitles = new String[planParts.length];
		this.entryValues = new String[planParts.length];
		TrainingReader reader = TrainingReader.getInstance(null);
		
		Map<String, PlanEntry> entryKeyToPlanEntry = reader.getViewCompositionMapByCategoryKey(this.catKey);
		
		for (int index = 0; index < planParts.length; ++index)
		{
			String[] entryParts = planParts[index].split("==");
			String entryTitleKey = entryParts[0];
			String optionKey = entryParts[1];
			
			Log.i("TAG",entryTitleKey + " for " + this.catKey);
			PlanEntry entry = entryKeyToPlanEntry.get(entryTitleKey);
			this.entryTitles[index] = entry.getNameKey();
			if (entry.getType() == Type.OPTIONS)
			{
				this.entryValues[index] = entry.getOptionFromOptionKey(optionKey);
			}
			else
			{
				this.entryValues[index] = (optionKey.equals("1") ? "True" : "False");
			}	
		}
	}
	private void initializeDate(String sessionDate)
	{
		if (sessionDate == null)
		{
			// Occurs for planned but not executed activity
			this.sessionDate = null;
			return;
		}
		Log.i("TAG","c");
		String[] parts = sessionDate.split("-");
		Log.i("TAG","c");
		int year = Integer.parseInt(parts[0]);
		Log.i("TAG","c");
		int month = Integer.parseInt(parts[1]);
		Log.i("TAG","c");
		int day = Integer.parseInt(parts[2]);
		Log.i("TAG","c");
		
		Calendar cal = GregorianCalendar.getInstance();
		Log.i("TAG","c");
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		Log.i("TAG","c");
		cal.set(Calendar.DAY_OF_MONTH, day);
		Log.i("TAG","c");
		this.sessionDate = cal;
	}

}
