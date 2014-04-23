package com.upenn.trainingtracker;

import java.util.Calendar;

public class Keys 
{
	/**
	 * Server-connection information
	 */
	public static final String CONNECTION_PASSWORD = "39Dkf93ja91dvMNa02ns1d4N9";
	//public static final String SITE = "http://pennvetwdc.t15.org/";
	public static final String SITE = "http://pennvetwdc.t15.org/";

	/**
	 * These are the keys used to identify the different columns of the database tables.  Each static subclass
	 * represents a different table and each field a column of that table
	 */
	public static class UserKeys
    {
    	public static final String ID = "id";
    	public static final String NAME = "name";
    	public static final String USERNAME = "username";
    	public static final String PASSWORD = "password";
    	public static final String EMAIL = "email";
    	public static final String PHONE = "phone";
    }
    public static class DogKeys
    {
    	public static final String ID = "id";
    	public static final String NAME = "name";
    	public static final String SKILLS_TABLE_NAME = "skills_table_name";
    	public static final String BIRTH_DATE = "birth_date";
    	public static final String BREED = "breed";
    	public static final String SERVICE_TYPE = "service_type";
    	public static final String IMAGE_NAME = "image_name";
    	public static final String SYNCED = "synced";
    	public static final String VERSION_NUMBER = "version_number";
    }
    public static class SkillsKeys
    {
    	public static final String CATEGORY_NAME = "category_name";
    	public static final String COMPLETED = "completed";
    	public static final String PLANNED = "planned";
    	public static final String VERSION_NUMBER = "version_number";
    }
    public static class CategoryKeys
    {
    	public static final String SESSION_DATE = "session_date";
    	public static final String PLAN = "plan";
    	public static final String TRIALS_RESULT = "trials_result";
    	public static final String SYNCED = "is_synced";
    	public static final String TRAINER_USERNAME = "trainer_username";
    }
    public static class SyncKeys
    {
    	public static final String CATEGORY_KEY = "category_name";
    	public static final String DOG_ID = "dog_id";
    }
    public static String getTableNameForCategory(String category, int dogID)
    {
    	// Can send null reference since has already been created by this point
    	TrainingReader reader = TrainingReader.getInstance(null);
    	String catKey = reader.categoryToCatKey(category);
    	
    	return catKey + "_" + dogID;
    }
    public static String getTableNameForCatKey(String catKey, int dogID)
    {
    	return catKey + "_" + dogID;
    }
    public static String getSkillsTableName(int dogID)
    {
    	return "skills_table_" + dogID;
    }
    public static String getCurrentDateString()
    {
		Calendar c = Calendar.getInstance(); 
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		int year = c.get(Calendar.YEAR);
		
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		String dateString = month + "-" + day + "-" + year + "-" + hours + ":" + minutes + ":" + second;
		
		return dateString;
    }
}
