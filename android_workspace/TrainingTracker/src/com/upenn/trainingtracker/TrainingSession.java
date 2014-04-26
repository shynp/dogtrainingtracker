package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.util.Log;
import android.view.View;

import com.upenn.trainingtracker.PlanEntry.Type;

public class TrainingSession 
{
	private String trainerUserName;
	private String trainerName;
	private String dogName;

	private Calendar sessionDate;
	private String sessionDateString;
	
	private String catKey;
	private String category;
	private List<Boolean> resultSequence;
	
	public boolean success;
	
	private Map<String, String> plan;
	
	private View view;
	
	public String getCategory()
	{
		return this.category;
	}
	public Calendar getDate()
	{
		return this.sessionDate;
	}
	public String getCategoryKey()
	{
		return this.catKey;
	}
	public Map<String, String> getPlanMap()
	{
		return this.plan;
	}
	public List<Boolean> getResultSequence()
	{
		return this.resultSequence;
	}
	public String getTrainerName()
	{
		return this.trainerName;
	}
	public TrainingSession(String catKey, String sessionDate, Map<String, String> plan, String trialsResult, String trainerUserName, String trainerName, String dogName)
	{
		this.trainerName = trainerName;
		Log.i("TAG","**************: " + trialsResult);
		this.catKey = catKey;
		TrainingReader reader = TrainingReader.getInstance(null);
		if (!reader.isInitialized())
		{
			throw new IllegalStateException("Cannot create TrainingSession object before TrainingReader has been initialized");
		}
		this.category = reader.catKeyToCategory(catKey);
		this.initializeDate(sessionDate);
		this.plan = plan;
		this.trainerUserName = trainerUserName;
		this.initializeTrialsResult(trialsResult);
		this.dogName = dogName;
	}
	private void initializeTrialsResult(String resultString)
	{
		this.resultSequence = new ArrayList<Boolean>();
		if (resultString.equals("EMPTY")) return;
		for (int index = 0; index < resultString.length(); ++index)
		{
			boolean result = resultString.charAt(index) == '1' ? true : false;
			this.resultSequence.add(result);
		}
	}
	public void setView(View view)
	{
		this.view = view;
	}
	public View getView()
	{
		return this.view;
	}
	public String getUserName()
	{
		return this.trainerUserName;
	}
	public String getSessionDateString()
	{
		return this.sessionDateString;
	}
	private void initializeDate(String sessionDate)
	{
		this.sessionDateString = sessionDate;
		if (sessionDate == null || sessionDate.equals("null"))
		{
			// Occurs for planned but not executed activity
			this.sessionDate = null;
			return;
		}
		String[] parts = sessionDate.split("-");
		int year = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int day = Integer.parseInt(parts[2]);
		
		String timeString = parts[3];
		String[] timeParts = timeString.split(":");
		int hour = Integer.parseInt(timeParts[0]);
		int minute = Integer.parseInt(timeParts[1]);
		int second = Integer.parseInt(timeParts[2]);
		
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		this.sessionDate = cal;
	}

}
