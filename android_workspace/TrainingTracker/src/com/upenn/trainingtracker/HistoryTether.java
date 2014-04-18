package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Filter;
import android.widget.Filterable;

public class HistoryTether
{
	private static HistoryTether instance;
	
	public static HistoryTether getInstance()
	{
		if (instance == null)
		{
			instance = new HistoryTether();
		}
		return instance;
	}
	/*
	 * Category Title, trainer username, success pattern, plan
	 */
	public HistoryAdapter getTrainingSessionAdapterForDog(int dogID, Activity activity)
	{
		Map<String, List<TrainingSession>> catKeyToSessions = new HashMap<String, List<TrainingSession>>();
		Map<String, List<TrainingSession>> userNameToSessions = new HashMap<String, List<TrainingSession>>();
		List<TrainingSession> allSessions = new ArrayList<TrainingSession>();
		Log.i("TAG","A");
		DatabaseHandler db = new DatabaseHandler(activity);
		Log.i("TAG","A");
		String skillsTableName = Keys.getSkillsTableName(dogID);
		Log.i("TAG","A");
		Cursor skillCursor = db.queryFromTable(skillsTableName, new String[] {Keys.SkillsKeys.CATEGORY_NAME}, null, null);
		// Iterate over each entry in skill table
		while (skillCursor.moveToNext())
		{
			Log.i("TAG","Ab");
			String catKey = skillCursor.getString(skillCursor.getColumnIndex(Keys.SkillsKeys.CATEGORY_NAME));
			String catTableName = Keys.getTableNameForCatKey(catKey, dogID);
			Log.i("TAG","A");
			Cursor catCursor = db.queryFromTable(catTableName, new String[]{Keys.CategoryKeys.PLAN,  Keys.CategoryKeys.SESSION_DATE,
					Keys.CategoryKeys.TRAINER_USERNAME, Keys.CategoryKeys.TRIALS_RESULT}, null, null);
			
			List<TrainingSession> catList = new ArrayList<TrainingSession>();
			// Iterate over each entry in category table
			while (catCursor.moveToNext())
			{
				Log.i("TAG","A");
				String plan = catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.PLAN));
				Log.i("TAG","Ac");
				String sessionDate = catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.SESSION_DATE));
				Log.i("TAG","A");
				String trainerUserName = catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.TRAINER_USERNAME));
				Log.i("TAG","A");
				String trialsResult = catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.TRIALS_RESULT));
				Log.i("TAG","Ad");
				TrainingSession session = new TrainingSession(catKey, sessionDate, plan, trialsResult, trainerUserName, null);
				Log.i("TAG","Ae");
				catList.add(session);
				Log.i("TAG","Af");
				allSessions.add(session);
				if (userNameToSessions.containsKey(trainerUserName))
				{
					
					Log.i("TAG","A");
					List<TrainingSession> userNameList = userNameToSessions.get(trainerUserName);
					userNameList.add(session);
				}
				else
				{
					Log.i("TAG","A");
					List<TrainingSession> userNameList = new ArrayList<TrainingSession>();
					userNameList.add(session);
					userNameToSessions.put(trainerUserName, userNameList);
				}
			}
			catKeyToSessions.put(catKey, catList);
		}
		return new HistoryAdapter(activity, allSessions, catKeyToSessions, userNameToSessions);
	}



}
