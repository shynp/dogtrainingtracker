package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;

public class UserTether 
{
	private static UserTether instance;
	
	private UserTether()
	{
		
	}
	public static UserTether getInstance()
	{
		if (instance == null)
		{
			instance = new UserTether();
		}
		return instance;
	}
	public List<String> getUserNames(Activity activity)
	{
		DatabaseHandler db = new DatabaseHandler(activity);
		Cursor userCursor = db.queryFromTable(DatabaseHandler.TABLE_USERS, new String[]{Keys.UserKeys.USERNAME}, null, null);
		
		List<String> userNames = new ArrayList<String>();
		
		while (userCursor.moveToNext())
		{
			String userName = userCursor.getString(userCursor.getColumnIndex(Keys.UserKeys.USERNAME));
			userNames.add(userName);
		}
		return userNames;
	}
	public List<String> getUserFullNames(Activity activity)
	{
		DatabaseHandler db = new DatabaseHandler(activity);
		Cursor userCursor = db.queryFromTable(DatabaseHandler.TABLE_USERS, new String[]{Keys.UserKeys.NAME}, null, null);
		
		List<String> userNames = new ArrayList<String>();
		
		while (userCursor.moveToNext())
		{
			String userName = userCursor.getString(userCursor.getColumnIndex(Keys.UserKeys.NAME));
			userNames.add(userName);
		}
		return userNames;
	}
}
