package com.upenn.trainingtracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import android.util.Base64;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper 
{ 
    // Database Version
    private static final int DATABASE_VERSION = 6;
 
    // Database Name
    private static final String DATABASE_NAME = "service_manager";
 
    // Contacts table name
    private static final String TABLE_USERS = "users";
    private static final String TABLE_DOGS = "dogs";

 
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
                Keys.DogKeys.IMAGE_NAME + " INTEGER" + ")";
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
    /**
     * Called by ConnectionsManager after the dog info is fetched.  This only updates the basic dog information
     * not the training data
     * @param JSON
     * @param activity
     */
    public void updateDogsWithJSON(String JSON, Activity activity)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("DELETE FROM " + this.TABLE_DOGS);
    	try 
    	{
    		Log.i("DATABASE","DATBASE CONTENTS ----------------------");
    		JSONArray jsonArray = new JSONArray(JSON);
        	for (int index = 0; index < jsonArray.length(); ++index)
        	{
            	JSONObject dogObject = jsonArray.getJSONObject(index);
            	int id = dogObject.getInt("id");
            	String name = dogObject.getString("name");
            	String skillsTableName = dogObject.getString("skills_table_name");
            	String birthDate = dogObject.getString("birth_date");
            	String breed = dogObject.getString("breed");
            	String serviceType = dogObject.getString("service_type");
            	String imageEncoded = dogObject.getString("image");
    			byte[] byteArray = Base64.decode(imageEncoded, 0);
				Bitmap img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
				//Log.i("TAG",imageEncoded);
            	String imgPath = "dog_image_" + id + ".png";
            	
            	// Save the image
            	this.saveImage(activity.getApplicationContext(), img, imgPath);
            	
            	// Update the database
            	Log.i("TAG", name + " " + name + " " + skillsTableName + " " + birthDate + " " + breed +
            			" " + serviceType + " " + imgPath);
            	
            	ContentValues row = new ContentValues();
            	row.put(Keys.DogKeys.ID, id);
            	row.put(Keys.DogKeys.NAME, name);
            	row.put(Keys.DogKeys.SKILLS_TABLE_NAME, skillsTableName);
            	row.put(Keys.DogKeys.BIRTH_DATE, birthDate);
            	row.put(Keys.DogKeys.BREED, breed);
            	row.put(Keys.DogKeys.SERVICE_TYPE, serviceType);
            	row.put(Keys.DogKeys.IMAGE_NAME, imgPath);
            	db.insert(this.TABLE_DOGS, null, row);
        	}
		} 
    	catch (JSONException e) {
			e.printStackTrace();
		}
    }
    /**
     * Saves the bitmap to InternalStorage.  This is storage that is only accessible to the application.
     * When the application is uninstalled, this information also dissapears.
     * @param context
     * @param bitmap
     * @param name
     */
    public void saveImage(Context context, Bitmap bitmap, String name)
    {
    	ContextWrapper cw = new ContextWrapper(context);
    	File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
    	File myPath = new File(directory, name);
    	
    	FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream(myPath);
    		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
    		fos.close();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }
    /**
     * Loads Bitmaps from internal storage
     * @param context
     * @param name
     * @return
     */
    public Bitmap loadImage(Context context, String name)
    {
    	ContextWrapper cw = new ContextWrapper(context);
    	Bitmap b = null;
        try {
        	File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File f=new File(directory, name);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        return b;
    }
    
    /**
     * Will return the information needed about each dog for the DogSelector activity
     * @return
     */
    public ArrayList<DogProfile> getDogProfiles(Context context)
    {
        String CREATE_DOGS_TABLE = "CREATE TABLE " + TABLE_DOGS + "("
                + Keys.UserKeys.ID + " INTEGER PRIMARY KEY," + 
        		Keys.DogKeys.NAME + " TEXT, " +
                Keys.DogKeys.SKILLS_TABLE_NAME + " TEXT, " +
        		Keys.DogKeys.BIRTH_DATE + " TEXT, " +
                Keys.DogKeys.BREED + " TEXT, " +
        		Keys.DogKeys.SERVICE_TYPE + " TEXT, " + 
                Keys.DogKeys.IMAGE_NAME + " INTEGER" + ")";
        
    	SQLiteDatabase db = this.getReadableDatabase();
    	String[] columnNames = new String[] {Keys.DogKeys.ID, Keys.DogKeys.NAME, Keys.DogKeys.SKILLS_TABLE_NAME,
    			Keys.DogKeys.BIRTH_DATE, Keys.DogKeys.BREED, Keys.DogKeys.SERVICE_TYPE, Keys.DogKeys.IMAGE_NAME};
    	Cursor cursor = db.query(DatabaseHandler.TABLE_DOGS, columnNames, null,null,null,null,null,null);    	
    	
    	ArrayList<DogProfile> profiles = new ArrayList<DogProfile>();
    	
    	while(cursor.moveToNext())
    	{
    		int ID = cursor.getInt(cursor.getColumnIndex(Keys.DogKeys.ID));
    		String name = cursor.getString(cursor.getColumnIndex(Keys.DogKeys.NAME));
    		String skillsTableName = cursor.getString(cursor.getColumnIndex(Keys.DogKeys.SKILLS_TABLE_NAME));
    		String birthDate = cursor.getString(cursor.getColumnIndex(Keys.DogKeys.BIRTH_DATE));
    		String breed = cursor.getString(cursor.getColumnIndex(Keys.DogKeys.BREED));
    		String serviceType = cursor.getString(cursor.getColumnIndex(Keys.DogKeys.SERVICE_TYPE));
    		String imageName = cursor.getString(cursor.getColumnIndex(Keys.DogKeys.IMAGE_NAME));
    		
    		Bitmap image = this.loadImage(context, imageName);
    		DogProfile prof = new DogProfile(ID, name, skillsTableName, birthDate, breed, serviceType, image);
    		profiles.add(prof);
    	}
    	return profiles;
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
