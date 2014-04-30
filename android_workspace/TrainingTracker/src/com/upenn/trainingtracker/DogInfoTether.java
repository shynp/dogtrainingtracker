package com.upenn.trainingtracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class DogInfoTether 
{
	private static DogInfoTether instance;
	
	private DogInfoTether()
	{
		
	}
	public static DogInfoTether getInstance()
	{
		if (instance == null)
		{
			instance = new DogInfoTether();
		}
		return instance;
	}
	
    /**
     * Called by ConnectionsManager after the dog info is fetched.  This only updates the basic dog information
     * not the training data
     * @param JSON
     * @param activity
     */
    public void updateDogsWithJSON(String JSON, Context activity)
    {
    	DatabaseHandler db = new DatabaseHandler(activity);
    	//db.execSQL("DELETE FROM " + this.TABLE_DOGS);
    	try 
    	{
    		Log.i("DATABASE","DATBASE CONTENTS ----------------------");
    		JSONObject jsonObject = new JSONObject(JSON);
    		JSONArray ids = jsonObject.getJSONArray("ids");
    		JSONArray jsonArray = jsonObject.getJSONArray("dogs");
    		
    		//********* REMOVING ENTRIES NOT CONTAINED IN "ids" JSONArray
    		// Get JSONArray as integer set
    		Set<Integer> serverIdSet = new HashSet<Integer>();
    		for (int index = 0; index < ids.length(); ++index)
    		{
    			serverIdSet.add(ids.getInt(index));
    		}
    		Cursor idCursor = db.queryFromTable(DatabaseHandler.TABLE_DOGS, new String[] {Keys.DogKeys.ID}, null, null);
    		List<Integer> removeList = new ArrayList<Integer>();
    		Set<Integer> deviceIdSet = new HashSet<Integer>();
    		while (idCursor.moveToNext())
    		{
    			int id = idCursor.getInt(idCursor.getColumnIndex(Keys.DogKeys.ID));
    			deviceIdSet.add(id);
    			if (!serverIdSet.contains(id))
    			{
    				removeList.add(id);
    			}
    		}
    		for (int id : removeList)
    		{
    			db.deleteEntries(DatabaseHandler.TABLE_DOGS, Keys.DogKeys.ID + " = " + id, null);
    			//TODO: Remove the skills table and any associated tables
    		}
    		//********* ITERATE OVER jsonArray update or add accordingly
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
            	int versionNumber = dogObject.getInt("version_number");
            	
    			byte[] byteArray = Base64.decode(imageEncoded, 0);
				Bitmap img = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            	String imgPath = "dog_image_" + id + ".png";
            	
            	// Save the image
            	this.saveImage(activity.getApplicationContext(), img, imgPath);
            	
            	// Update the dog table
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
            	row.put(Keys.DogKeys.VERSION_NUMBER, versionNumber);
            	
            	if (deviceIdSet.contains(id))
            	{
                	db.updateTable(DatabaseHandler.TABLE_DOGS, row, Keys.DogKeys.ID + " = " + id, null);
            	}
            	else
            	{
            		db.insertIntoTable(DatabaseHandler.TABLE_DOGS, null, row);
            		// Create the skills table if it does not exist
            		db.createSkillsTable(skillsTableName);
            	}
        	}
		} 
    	catch (JSONException e) {
			e.printStackTrace();
		}
    	db.close();
    }
    /**
     * Will return the information needed about each dog for the DogSelector activity
     * @return
     */
    public ArrayList<DogProfile> getDogProfiles(Activity activity)
    {
    	DatabaseHandler db = new DatabaseHandler(activity);
    	String[] columnNames = new String[] {Keys.DogKeys.ID, Keys.DogKeys.NAME, Keys.DogKeys.SKILLS_TABLE_NAME,
    			Keys.DogKeys.BIRTH_DATE, Keys.DogKeys.BREED, Keys.DogKeys.SERVICE_TYPE, Keys.DogKeys.IMAGE_NAME};
    	
    	Cursor cursor = db.queryFromTable(DatabaseHandler.TABLE_DOGS, columnNames, null, null);    	
    	
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
    		
    		Bitmap image = this.loadImage(activity, imageName);
    		DogProfile prof = new DogProfile(ID, name, skillsTableName, birthDate, breed, serviceType, image);
    		profiles.add(prof);
    	}
    	db.close();
    	return profiles;
    }
    /**
     * Mapping of dogID to version_number
     * @return
     */
    public JSONObject getDogEntryVersionNumbers(Context activity)
    {
    	DatabaseHandler db = new DatabaseHandler(activity);
    	Cursor cursor = db.queryFromTable(DatabaseHandler.TABLE_DOGS, new String[] {Keys.DogKeys.ID,  Keys.DogKeys.VERSION_NUMBER},
    			null, null);
    	JSONObject object = new JSONObject();
    	while (cursor.moveToNext())
    	{
    		int dogID = cursor.getInt(cursor.getColumnIndex(Keys.DogKeys.ID));
    		int versionNumber = cursor.getInt(cursor.getColumnIndex(Keys.DogKeys.VERSION_NUMBER));
    		try 
    		{
				object.put(Integer.toString(dogID), Integer.toString(versionNumber));
			} 
    		catch (JSONException e) 
    		{
				e.printStackTrace();
			}
    	}
    	db.close();
    	return object;
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
	
	
}
