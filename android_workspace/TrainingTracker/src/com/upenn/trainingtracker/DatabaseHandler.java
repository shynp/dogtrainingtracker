package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper 
{ 
    // Database Version
    private static final int DATABASE_VERSION = 5;
 
    // Database Name
    private static final String DATABASE_NAME = "service_manager";
 
    // Contacts table name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DOGS = "dogs";

 
    public DatabaseHandler(Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Creating Tables
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
                Keys.DogKeys.SYNCED + " INTEGER" + ")";
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_DOGS_TABLE);
    }
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) 
	{
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
    	db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOGS);
    	onCreate(db);
	}
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
    public void updateDogsWithJSON(String JSON)
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
            	String skillsTableName = userObject.getString("skills_table_name");
            	String birthDate = userObject.getString("birth_date");
            	String breed = userObject.getString("breed");
            	String serviceType = userObject.getString("service_type");
            	String imageName = userObject.getString("image_name");
            	
            	Log.i("TAG", name + " " + name + " " + skillsTableName + " " + birthDate + " " + breed +
            			" " + serviceType + " " + imageName);
            	ContentValues row = new ContentValues();
            	row.put(Keys.DogKeys.ID, id);
            	row.put(Keys.DogKeys.NAME, name);
            	row.put(Keys.DogKeys.SKILLS_TABLE_NAME, skillsTableName);
            	row.put(Keys.DogKeys.BIRTH_DATE, birthDate);
            	row.put(Keys.DogKeys.BREED, breed);
            	row.put(Keys.DogKeys.SERVICE_TYPE, serviceType);
            	row.put(Keys.DogKeys.IMAGE_NAME, imageName);
            	
            	db.insert(this.TABLE_DOGS, null, row);
        	}
		} 
    	catch (JSONException e) {
			e.printStackTrace();
		}
    }
    public ArrayList<DogProfile> getDogProfiles()
    {
    	// TODO: Implement this method
    	return null;
    }
    public boolean isValidUser(String userName, String password)
    {
    	SQLiteDatabase db = this.getReadableDatabase();
    	String[] columnNames = new String[] {Keys.UserKeys.USERNAME, Keys.UserKeys.PASSWORD};
    	String whereClause =  Keys.UserKeys.USERNAME + "='" + userName + "' AND " +
    			Keys.UserKeys.PASSWORD + "='" + password + "'";
    	Cursor cursor = db.query(TABLE_USERS, columnNames, whereClause,null,null,null,null,null);
    	return cursor.moveToFirst();
    }
    
    public void syncDogs()
    {
    	
    }

}
