package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrainingInfoTether 
{
	private static TrainingInfoTether instance;
	
	private TrainingInfoTether()
	{
		
	}
	
	public static TrainingInfoTether getInstance()
	{
		if (instance == null)
		{
			instance = new TrainingInfoTether();
		}
		return instance;
	}
	
    /**
     * Updates dog training info.  First pushes unsynchronized information and then pulls everything down
     * @param JSON
     * @param activity
     */
    public void updateDogTrainingInfo(JSONObject object, Context activity)
    {
    	DatabaseHandler db = new DatabaseHandler(activity);
    	Cursor idCursor = db.queryFromTable(DatabaseHandler.TABLE_DOGS, new String[] {Keys.DogKeys.ID}, null, null);
    	Iterator idIter = object.keys();
    	while (idIter.hasNext())
    	{
    		int id = Integer.parseInt((String) idIter.next());
    		JSONObject allAssocs = null;
    		try 
    		{
				allAssocs = object.getJSONObject((Integer.toString(id)));
			} 
    		catch (JSONException e) 
			{
				e.printStackTrace();
			}
    		this.updateDogWithAssocs(allAssocs, id, db);
    	}
    	db.close();
    }
    /*
     * Assocs goes from catKey -> JSONArray of rows
     */
    private void updateDogWithAssocs(JSONObject allAssocs, int id, DatabaseHandler db)
    {    	
    	Iterator iter = allAssocs.keys();
    	while (iter.hasNext())
    	{
    		String catKey = (String) iter.next();
    		JSONArray rows = null;
    		int versionNumber = -1;
			try 
			{
				JSONObject wrapper = allAssocs.getJSONObject(catKey);
				rows = wrapper.getJSONArray("rows");
				versionNumber = wrapper.getInt("version_number");
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
    		String catTableName = Keys.getTableNameForCatKey(catKey, id);
    		
    		String skillsTableName = Keys.getSkillsTableName(id);
    		String whereClause = Keys.SkillsKeys.CATEGORY_NAME + "=?";
    		Cursor skillsCursor = db.queryFromTable(skillsTableName, new String[] {Keys.SkillsKeys.CATEGORY_NAME}, whereClause, new String[] {catKey});
    		if (skillsCursor.getCount() == 0)
    		{
    			ContentValues values = new ContentValues();
    			values.put(Keys.SkillsKeys.CATEGORY_NAME, catKey);
    			values.put(Keys.SkillsKeys.COMPLETED, false);
    			values.put(Keys.SkillsKeys.PLANNED, false);
    			values.put(Keys.SkillsKeys.VERSION_NUMBER, versionNumber);
    			db.insertIntoTable(skillsTableName, null, values);
    			db.createCategoryTableIfNotExists(catTableName);
    		}
    		// Clear the table
    		db.clearTable(catTableName);
    		// Add the new rows
    		boolean isPlanned = this.addRowsToTable(rows, catTableName, db);
    		// Indicate that is planned in skills table
    		if (isPlanned)
    		{
    			ContentValues values = new ContentValues();
    			values.put(Keys.SkillsKeys.PLANNED, true);
    			whereClause = Keys.SkillsKeys.CATEGORY_NAME + "=?";
    			db.updateTable(skillsTableName, values, whereClause, new String[] {catKey});
    		}
    		else
    		{
    			ContentValues values = new ContentValues();
    			values.put(Keys.SkillsKeys.PLANNED, false);
    			whereClause = Keys.SkillsKeys.CATEGORY_NAME + "=?";
    			db.updateTable(skillsTableName, values, whereClause, new String[] {catKey});
    		}
    	}
    }
    private boolean addRowsToTable(JSONArray rows, String tableName, DatabaseHandler db)
    {
    	for (int index = 0; index < rows.length(); ++index)
    	{
    		JSONObject entry = null;
    		String plan = null;
    		String sessionDate = null;
    		String trialsResult = null;
    		String userName = null;
			try 
			{
				entry = rows.getJSONObject(index);
				plan = entry.getString("plan");
	    		sessionDate = entry.getString("session_date");
	    		trialsResult = entry.getString("trials_result");
	    		userName = entry.getString("trainer_username");
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			Log.i("TAG","Plan: " + plan);
			
    		ContentValues values = new ContentValues();
    		values.put(Keys.CategoryKeys.PLAN, plan);
    		values.put(Keys.CategoryKeys.SESSION_DATE, sessionDate);
    		values.put(Keys.CategoryKeys.SYNCED, 1);
    		values.put(Keys.CategoryKeys.TRIALS_RESULT, trialsResult);
    		Log.i("TAG",trialsResult);
    		values.put(Keys.CategoryKeys.TRAINER_USERNAME, userName);
    		db.insertIntoTable(tableName, null, values);
    		Log.i("TAG","Data inserted");
    		if (index == rows.length() - 1 && trialsResult.equals("EMPTY"))
    		{
    			return true;
    		}
    		else if (index == rows.length() - 1)
    		{
    			return false;
    		}
    	}
    	return false;
    }
    /**
     * Returns the update JSON object that will be sent to the server.  At highest level contains associative array
     * between categoryTableName and an array.  This array contains a bunch of associative arrays each with the same
     * structure mapping CategoryKeys column keys to their values
     * @return
     */
    public JSONObject getCategoryUpdateJSON(Context activity)
    {
    	DatabaseHandler db = new DatabaseHandler(activity);
    	Cursor cursor = db.queryFromTable(DatabaseHandler.TABLE_SYNC, new String[] {Keys.SyncKeys.CATEGORY_KEY, Keys.SyncKeys.DOG_ID}, 
    			null, null);
    	
    	JSONObject tableToValues = new JSONObject();
    	
    	// See which tables have data that needs to be pushed
    	while (cursor.moveToNext())
    	{
    		String catKey = cursor.getString(cursor.getColumnIndex(Keys.SyncKeys.CATEGORY_KEY));
    		int dogID = cursor.getInt(cursor.getColumnIndex(Keys.SyncKeys.DOG_ID));
    		
    		// Increment the version number for the corresponding entry in skills table
    		String skillsTableName = Keys.getSkillsTableName(dogID);
    		String whereClause = Keys.SkillsKeys.CATEGORY_NAME + " = '" + catKey + "'";
    		db.incrementEntryValue(skillsTableName, Keys.SkillsKeys.VERSION_NUMBER, whereClause);
    		
    		
    		String catTableName = Keys.getTableNameForCatKey(catKey, dogID);

    		Cursor catCursor = db.queryFromTable(catTableName, new String[] {Keys.CategoryKeys.SESSION_DATE, Keys.CategoryKeys.PLAN, 
    				Keys.CategoryKeys.TRIALS_RESULT, Keys.CategoryKeys.SYNCED, Keys.CategoryKeys.TRAINER_USERNAME}, 
    				Keys.CategoryKeys.SYNCED + "=" + "0", null);
    		JSONArray tableRows = new JSONArray();
    		while (catCursor.moveToNext())
    		{
        		JSONObject row = new JSONObject();
        		try 
        		{
					row.put(Keys.CategoryKeys.SESSION_DATE, catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.SESSION_DATE)));
	        		row.put(Keys.CategoryKeys.PLAN, catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.PLAN)));
	        		row.put(Keys.CategoryKeys.TRIALS_RESULT, catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.TRIALS_RESULT)));
	        		row.put(Keys.CategoryKeys.TRAINER_USERNAME, catCursor.getString(catCursor.getColumnIndex(Keys.CategoryKeys.TRAINER_USERNAME)));
        		} 
        		catch (JSONException e) 
        		{
					e.printStackTrace();
				}
        		tableRows.put(row);
    		}
    		try 
    		{
    			JSONObject wrapper = new JSONObject();
    			wrapper.put("dogID", dogID);
    			wrapper.put("categoryKey", catKey);
    			wrapper.put("tableRows", tableRows);
				tableToValues.put(catTableName, wrapper);
			} 
    		catch (JSONException e) 
    		{
				e.printStackTrace();
			}
    		// Get rid of sync flag
    		ContentValues values = new ContentValues();
    		values.put(Keys.CategoryKeys.SYNCED, true);
    		db.updateTable(catTableName, values, null, null);
    	}	
    	db.close();
    	return tableToValues;
    }
	/*
	 * result: dogID -> dogObject
	 * dogObject: catKey -> version_number
	 */
	public JSONObject getCategoryVersionNumbers(Context activity)
	{
		DatabaseHandler db = new DatabaseHandler(activity);
		Cursor cursor = db.queryFromTable(DatabaseHandler.TABLE_DOGS, new String[] {Keys.DogKeys.ID,  Keys.DogKeys.SKILLS_TABLE_NAME}, null, null);
		JSONObject result = new JSONObject();
		while (cursor.moveToNext())
		{
			int dogID = cursor.getInt(cursor.getColumnIndex(Keys.DogKeys.ID));
			String skillsTable = cursor.getString(cursor.getColumnIndex(Keys.DogKeys.SKILLS_TABLE_NAME));
			
			Cursor skillCursor = db.queryFromTable(skillsTable, new String[] {Keys.SkillsKeys.CATEGORY_NAME, Keys.SkillsKeys.VERSION_NUMBER}, null, null);
			JSONObject dogObject = new JSONObject();
			while (skillCursor.moveToNext())
			{
				String catKey = skillCursor.getString(skillCursor.getColumnIndex(Keys.SkillsKeys.CATEGORY_NAME));
				int versionNumber = skillCursor.getInt(skillCursor.getColumnIndex(Keys.SkillsKeys.VERSION_NUMBER));
				try 
				{
					dogObject.put(catKey, versionNumber);
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
			try 
			{
				result.put(Integer.toString(dogID),dogObject);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		db.close();
		return result;
	}
    public boolean hasPlan(String category, int dogID, Context activity)
    {
    	return !(this.getPlan(category, dogID, activity) == null);
    }
    //TODO: Get rid of method below so only getPlanByCategoryKey is used
    /*
     * Mapping of NameKey to ValueKey
     */
    public Map<String, String> getPlan(String catKey, int dogID, Context activity)
    {
    	TrainingReader reader = TrainingReader.getInstance(null);
    	String skillsTableName = Keys.getSkillsTableName(dogID);
    	DatabaseHandler db = new DatabaseHandler(activity);
    	
    	String whereClause = Keys.SkillsKeys.PLANNED + " = '" + 1 + "' AND " + Keys.SkillsKeys.CATEGORY_NAME + " = '" + catKey + "'";
    	Cursor result = db.queryFromTable(skillsTableName, new String[]{Keys.SkillsKeys.PLANNED}, whereClause, null);
    	if (result.getCount() == 0)
    	{
    		return null;
    	}
    	String catTableName = Keys.getTableNameForCatKey(catKey, dogID);
    	result = db.queryFromTable(catTableName, new String[] {Keys.CategoryKeys.PLAN},Keys.CategoryKeys.TRIALS_RESULT + " = 'EMPTY'", null);
    	result.moveToFirst();
    	String plan = result.getString(result.getColumnIndex(Keys.CategoryKeys.PLAN));
    	
		String[] planParts = plan.split(Pattern.quote("||"));
		Map<String, String> mapping = new HashMap<String, String>();
		for (String part : planParts)
		{
			String[] subParts = part.split("==");
			mapping.put(subParts[0], subParts[1]);
		}
		db.close();
		return mapping;
    }
    /*
     * Mapping of NameKey to ValueKey
     */
    public Map<String, String> getPlanByCategoryKey(String catKey, int dogID, Context activity)
    {
    	TrainingReader reader = TrainingReader.getInstance(null);
    	String skillsTableName = Keys.getSkillsTableName(dogID);
    	DatabaseHandler db = new DatabaseHandler(activity);
    	
    	String whereClause = Keys.SkillsKeys.PLANNED + " = '" + 1 + "' AND " + Keys.SkillsKeys.CATEGORY_NAME + " = '" + catKey + "'";
    	Cursor result = db.queryFromTable(skillsTableName, new String[]{Keys.SkillsKeys.PLANNED}, whereClause, null);
    	if (result.getCount() == 0)
    	{
    		return null;
    	}
    	String catTableName = Keys.getTableNameForCatKey(catKey, dogID);
    	result = db.queryFromTable(catTableName, new String[] {Keys.CategoryKeys.PLAN},Keys.CategoryKeys.TRIALS_RESULT + " = 'EMPTY'", null);
    	result.moveToFirst();
    	String plan = result.getString(result.getColumnIndex(Keys.CategoryKeys.PLAN));
    	db.close();
    	return this.planStringToPlanMap(plan);
    	
    }
    public Map<String, String> planStringToPlanMap(String plan)
    {
		String[] planParts = plan.split(Pattern.quote("||"));
		Map<String, String> mapping = new HashMap<String, String>();
		for (String part : planParts)
		{
			String[] subParts = part.split("==");
			mapping.put(subParts[0], subParts[1]);
		}
		return mapping;
    }
    public List<String> getPlannedCategoryKeys(Context context, int dogID)
    {
    	String skillsTableName = Keys.getSkillsTableName(dogID);

    	DatabaseHandler db = new DatabaseHandler(context);
    	Cursor result = db.queryFromTable(skillsTableName, new String[] {Keys.SkillsKeys.CATEGORY_NAME}, Keys.SkillsKeys.PLANNED + "='" + 1 + "'", null);
    	List<String> planned = new ArrayList<String>();
    	while (result.moveToNext())
    	{
    		planned.add(result.getString(result.getColumnIndex(Keys.SkillsKeys.CATEGORY_NAME)));
    	}
    	return planned;
    }
    /*
     * 
     */
    public void addPlan(String date, String plan, String catKey, int dogID, String userName, Context activity)
    {    
    	Log.i("TAG","1");
    	String skillsTableName = Keys.getSkillsTableName(dogID);
    	String categoryTableName = Keys.getTableNameForCatKey(catKey, dogID);
    	Log.i("TAG","1");
    	TrainingReader reader = TrainingReader.getInstance(null);
    	// Get skills table and see if it contains the category table
    	DatabaseHandler db = new DatabaseHandler(activity);
    	Cursor result = db.queryFromTable(skillsTableName, new String[] {Keys.SkillsKeys.CATEGORY_NAME}, Keys.SkillsKeys.CATEGORY_NAME + "='" + catKey + "'", null);
    	Log.i("TAG","Checking for: " + skillsTableName);
    	// If it doesn't add the entry and then create the category table
    	if (result.getCount() == 0)
    	{
    		Log.i("TAG","1");
    		// Add the entry
    		ContentValues values = new ContentValues();
        	values.put(Keys.SkillsKeys.CATEGORY_NAME, catKey);
        	values.put(Keys.SkillsKeys.PLANNED, 1);
        	values.put(Keys.SkillsKeys.COMPLETED, 0);
        	values.put(Keys.SkillsKeys.VERSION_NUMBER, 1);
        	db.insertIntoTable(skillsTableName, null, values);
        	
        	// Create the category table
        	db.createCategoryTable(categoryTableName);
    	}
    	else
    	{
    		Log.i("TAG","1");
	    	// If the table does exist delete any previous plans that weren't executed
	    	db.deleteEntries(categoryTableName, Keys.CategoryKeys.TRIALS_RESULT + " = " + "'EMPTY'", null);
	    	// Ensure that skillsTable indicates that the activity is planned
    		ContentValues values = new ContentValues();
        	values.put(Keys.SkillsKeys.CATEGORY_NAME, catKey);
        	values.put(Keys.SkillsKeys.PLANNED, 1);
        	db.updateTable(skillsTableName, values, Keys.SkillsKeys.CATEGORY_NAME + " = '" + catKey + "'", null);
    	}
    	Log.i("TAG","1");
    	// Add the new plan to the category table
    	Log.i("TAG","2");
    	ContentValues values = new ContentValues();
    	Log.i("TAG","3");
    	values.put(Keys.CategoryKeys.PLAN, plan);
    	values.put(Keys.CategoryKeys.TRIALS_RESULT, "EMPTY"); // The flag used to see unexecuted plan
    	values.put(Keys.CategoryKeys.SYNCED, 0);
    	values.put(Keys.CategoryKeys.TRAINER_USERNAME, userName);
    	values.put(Keys.CategoryKeys.SESSION_DATE, date);
    	db.insertIntoTable(categoryTableName, null, values);  
    	Log.i("TAG","3");
    	// Tell sync table that this category table needs to be updated
    	// First check if it already knows
    	String whereClause = Keys.SyncKeys.CATEGORY_KEY + "='" + catKey + "' AND " + Keys.SyncKeys.DOG_ID + "='" + dogID + "'";
    	Log.i("TAG","1");
    	result = db.queryFromTable(DatabaseHandler.TABLE_SYNC, new String[] {Keys.SyncKeys.CATEGORY_KEY}, whereClause, null);
    	Log.i("TAG","1");
    	// If not add the entry
    	if (result.getCount() == 0)
    	{
    		values = new ContentValues();
    		values.put(Keys.SyncKeys.CATEGORY_KEY, catKey);
    		values.put(Keys.SyncKeys.DOG_ID, dogID);
    		db.insertIntoTable(DatabaseHandler.TABLE_SYNC, null, values);
    	}
    	Log.i("TAG","5");
    	db.close();
    }
    public void addEntry(String date, String catKey, int dogID, String userName, List<Boolean> resultSequence, Context activity)
    {
    	String skillsTableName = Keys.getSkillsTableName(dogID);
    	String categoryTableName = Keys.getTableNameForCatKey(catKey, dogID);
    			
    	DatabaseHandler db = new DatabaseHandler(activity);
    	// Make sure that skills table indicates that it is not planned
    	ContentValues values = new ContentValues();
    	values.put(Keys.SkillsKeys.CATEGORY_NAME, catKey);
    	values.put(Keys.SkillsKeys.PLANNED, 0);
    	db.updateTable(skillsTableName, values, Keys.SkillsKeys.CATEGORY_NAME + " = '" + catKey + "'", null);	
    	// Add the entry in the category table where "EMPTY" word is
    	String resultString = "";
    	for (Boolean bool : resultSequence)
    	{
    		resultString += bool ? "1" : "0";
    	}
    	values = new ContentValues();
    	values.put(Keys.CategoryKeys.TRIALS_RESULT, resultString);
    	values.put(Keys.CategoryKeys.TRAINER_USERNAME, userName);
    	values.put(Keys.CategoryKeys.SESSION_DATE, Keys.getCurrentDateString());
    	String whereClause = Keys.CategoryKeys.TRIALS_RESULT + " = " + "'EMPTY'";
    	db.updateTable(categoryTableName, values, whereClause, null);
    	// Tell sync table that this needs to be updated
    	whereClause = Keys.SyncKeys.CATEGORY_KEY + "='" + catKey + "' AND " + Keys.SyncKeys.DOG_ID + "='" + dogID + "'";
    	Cursor result = db.queryFromTable(DatabaseHandler.TABLE_SYNC, new String[] {Keys.SyncKeys.CATEGORY_KEY}, whereClause, null);
    	// If not add the entry
    	if (result.getCount() == 0)
    	{
    		values = new ContentValues();
    		values.put(Keys.SyncKeys.CATEGORY_KEY, catKey);
    		values.put(Keys.SyncKeys.DOG_ID, dogID);
    		db.insertIntoTable(DatabaseHandler.TABLE_SYNC, null, values);
    	}
    	db.close();
    }
}
