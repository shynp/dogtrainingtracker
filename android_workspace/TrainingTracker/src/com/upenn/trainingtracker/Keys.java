package com.upenn.trainingtracker;

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
    }
    public static class SkillsKeys
    {
    	public static final String SKILL_NAME = "skill_name";
    	public static final String SESSIONS_TABLE_NAME = "sessions_table_name";
    	public static final String SYNCED = "synced";
    }
}
