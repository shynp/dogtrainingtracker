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
		TrainingInfoTether infoTether = TrainingInfoTether.getInstance();
		Map<String, List<TrainingSession>> catKeyToSessions = new HashMap<String, List<TrainingSession>>();
		Map<String, List<TrainingSession>> userNameToSessions = new HashMap<String, List<TrainingSession>>();
		List<TrainingSession> allSessions = new ArrayList<TrainingSession>();
		DatabaseHandler db = new DatabaseHandler(activity);
		String skillsTableName = Keys.getSkillsTableName(dogID);
		Cursor skillCursor = db.queryFromTable(skillsTableName, new String[] {Keys.SkillsKeys.CATEGORY_NAME}, null, null);
		UserTether userTether = UserTether.getInstance();
		Map<String, String> userNameToFullName = userTether.getUserNameToUserFullName(activity);
		// Iterate over each entry in skill table
		while (skillCursor.moveToNext())
		{
			String catKey = skillCursor.getString(skillCursor.getColumnIndex(Keys.SkillsKeys.CATEGORY_NAME));
			String catTableName = Keys.getTableNameForCatKey(catKey, dogID);
			Cursor catCursor = db.queryFromTable(catTableName, new String[]{Keys.CategoryKeys.PLAN,  Keys.CategoryKeys.SESSION_DATE,
					Keys.CategoryKeys.TRAINER_USERNAME, Keys.CategoryKeys.TRIALS_RESULT}, null, null);
			
			List<TrainingSession> catList = new ArrayList<TrainingSession>();
			// Iterate over each entry in category table
			while (catCursor.moveToNext())
			{
				String plan = catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.PLAN));
				Map<String, String> planMap = infoTether.planStringToPlanMap(plan);
				String sessionDate = catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.SESSION_DATE));
				String trainerUserName = catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.TRAINER_USERNAME));
				String trialsResult = catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.TRIALS_RESULT));
				String fullName = userNameToFullName.get(trainerUserName);
				TrainingSession session = new TrainingSession(catKey, sessionDate, planMap, trialsResult, trainerUserName, fullName, null);
				catList.add(session);
				allSessions.add(session);
				if (userNameToSessions.containsKey(trainerUserName))
				{
					List<TrainingSession> userNameList = userNameToSessions.get(trainerUserName);
					userNameList.add(session);
				}
				else
				{
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
