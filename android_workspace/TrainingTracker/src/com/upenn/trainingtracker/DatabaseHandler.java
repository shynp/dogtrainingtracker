package com.upenn.trainingtracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHandler extends SQLiteOpenHelper
{ 
    // Database Version
    private static final int DATABASE_VERSION = 41;
 
    // Database Name
    private static final String DATABASE_NAME = "service_manager.db";
 
    // Contacts table name
    public static final String TABLE_USERS = "users";
    public static final String TABLE_DOGS = "dogs";
    public static final String TABLE_SYNC = "sync_table";

    private static final int RESULT_TRAINING_INFO_UPDATE = 1;
 
    public DatabaseHandler(Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * This method is only called when first creating the database, NOT everytime the application is run.
     * If the structure of the database is changed, for example if additional columns or tables are added, then
     * these should be specified in this method and then the DATABASE_VERSION number above should be increased
     * by 1.  This tells Android to run the onUpgrade method which drops all the tables and then calls the onCreate
     * method.
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + Keys.UserKeys.ID + " INTEGER PRIMARY KEY," + 
        		Keys.UserKeys.NAME + " TEXT, " +
                Keys.UserKeys.USERNAME + " TEXT, " +
        		Keys.UserKeys.PASSWORD + " TEXT, " + 
                Keys.UserKeys.EMAIL + "  TEXT, " +
        		Keys.UserKeys.PHONE + " TEXT" + ")";
        String CREATE_DOGS_TABLE = "CREATE TABLE " + TABLE_DOGS + "("
                + Keys.UserKeys.ID + " INTEGER PRIMARY KEY," + 
        		Keys.DogKeys.NAME + " TEXT, " +
                Keys.DogKeys.SKILLS_TABLE_NAME + " TEXT, " +
        		Keys.DogKeys.BIRTH_DATE + " TEXT, " +
                Keys.DogKeys.BREED + " TEXT, " +
        		Keys.DogKeys.SERVICE_TYPE + " TEXT, " + 
                Keys.DogKeys.IMAGE_NAME + " INTEGER, " +
        		Keys.DogKeys.VERSION_NUMBER + " INTEGER)";
        String CREATE_SYNC_TABLE = "CREATE TABLE " + TABLE_SYNC + "("
                + Keys.SyncKeys.CATEGORY_KEY + " TEXT," + 
        		Keys.SyncKeys.DOG_ID + " INTEGER)";
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_DOGS_TABLE);
        db.execSQL(CREATE_SYNC_TABLE);
    }
	
    @Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) 
	{
    	Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
    	String tableName="";
    	  if (c.moveToFirst()) {

    	      while ( !c.isAfterLast() ) {

    	          tableName = c.getString( c.getColumnIndex("name"));
    	          Log.i("TAG",tableName);
    	          if(!tableName.equals("android_metadata"))
    	          {
    	            db.execSQL("DROP TABLE '"+tableName+"'");
    	          }
    	          c.moveToNext();
    	      }
    	  }

    	c.close();

    	onCreate(db);
	}


	/**
	 * This is called by ConnectionsManager after the users are fetched
	 * @param JSON
	 */
    public void updateUsersWithJSON(String JSON)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DELETE FROM " + this.TABLE_USERS);
    	try 
    	{
    		Log.i("DATABASE","DATBASE CONTENTS ----------------------");
    		JSONArray jsonArray = new JSONArray(JSON);
        	for (int index = 0; index < jsonArray.length(); ++index)
        	{
            	JSONObject userObject = jsonArray.getJSONObject(index);
            	int id = userObject.getInt("id");
            	String name = userObject.getString("name");
            	String username = userObject.getString("username");
            	String password = userObject.getString("password");
            	String email = userObject.getString("email");
            	String phone = userObject.getString("phone");
            	
            	Log.i("TAG", name + " " + username + " " + password + " " + email + " " + phone);
            	ContentValues row = new ContentValues();
            	row.put(Keys.UserKeys.ID, id);
            	row.put(Keys.UserKeys.NAME, name);
            	row.put(Keys.UserKeys.USERNAME, username);
            	row.put(Keys.UserKeys.PASSWORD, password);
            	row.put(Keys.UserKeys.EMAIL, email);
            	row.put(Keys.UserKeys.PHONE, phone);
            	
            	db.insert(this.TABLE_USERS, null, row);
        	}
		} 
    	catch (JSONException e) {
			e.printStackTrace();
		}
    }
    public void deleteEntries(String tableName, String whereClause, String[] whereArgs)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(tableName, whereClause, whereArgs);
    }
    public void createSkillsTable(String tableName)
    {
        String CREATE_SKILLS_TABLE = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + Keys.SkillsKeys.CATEGORY_NAME + " TEXT, " + 
        		Keys.SkillsKeys.PLANNED + " INTEGER, " + Keys.SkillsKeys.COMPLETED + " INTEGER, " +
                Keys.SkillsKeys.VERSION_NUMBER + " INTEGER" + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_SKILLS_TABLE);
    }

    public void createCategoryTableIfNotExists(String tableName)
    {
    	Log.i("TAG","Creating: " + tableName);
    	SQLiteDatabase db = this.getWritableDatabase();
    	// Create the category table
    	String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + tableName + "("
                + Keys.CategoryKeys.PLAN + " TEXT," + 
        		Keys.CategoryKeys.SESSION_DATE + " TEXT, " +
                Keys.CategoryKeys.TRIALS_RESULT + " TEXT, " +
                Keys.CategoryKeys.SYNCED + "  INTEGER, " + 
                Keys.CategoryKeys.TRAINER_USERNAME + " TEXT)";
    	db.execSQL(CREATE_CATEGORY_TABLE);
    }
    public void clearTable(String tableName)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.delete(tableName, null, null);
    }
    public Cursor queryFromTable(String tableName, String[] columnNames, String whereClause, String[] whereArgs)
    {
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor result = db.query(tableName, columnNames, whereClause, whereArgs, null, null, null, null);
    	return result;
    }
    public void insertIntoTable(String tableName, String nullColumnHack, ContentValues values)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.insert(tableName, nullColumnHack, values);
    	db.close();
    }
    public void updateTable(String tableName, ContentValues values, String whereClause, String[] whereArgs)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.update(tableName, values, whereClause, whereArgs);
    	db.close();
    }
    public void createCategoryTable(String categoryTableName)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	String CREATE_CATEGORY_TABLE = "CREATE TABLE " + categoryTableName + "("
                + Keys.CategoryKeys.PLAN + " TEXT," + 
        		Keys.CategoryKeys.SESSION_DATE + " TEXT, " +
                Keys.CategoryKeys.TRIALS_RESULT + " TEXT, " +
                Keys.CategoryKeys.SYNCED + "  INTEGER, " +
                Keys.CategoryKeys.TRAINER_USERNAME + " TEXT)";
    	db.execSQL(CREATE_CATEGORY_TABLE);
    }
    public void incrementEntryValue(String tableName, String columnName, String whereClause)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql = "UPDATE " + tableName + " SET " + columnName + " = " + columnName + " + 1 WHERE " + whereClause;
    	db.execSQL(sql);
    }
    /**
     * Called by LogInActivity to valide credentials
     * @param userName
     * @param password
     * @return
     */
    public boolean isValidUser(String userName, String password)
    {
    	SQLiteDatabase db = this.getReadableDatabase();
    	String[] columnNames = new String[] {Keys.UserKeys.USERNAME, Keys.UserKeys.PASSWORD};
    	String whereClause =  Keys.UserKeys.USERNAME + "='" + userName + "' AND " +
    			Keys.UserKeys.PASSWORD + "='" + password + "'";
    	Cursor cursor = db.query(TABLE_USERS, columnNames, whereClause,null,null,null,null,null);
    	return cursor.moveToFirst();
    }
}
