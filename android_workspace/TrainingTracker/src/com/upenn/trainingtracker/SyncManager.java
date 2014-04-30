package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class SyncManager implements Notifiable
{
	private static SyncManager instance;
	private Context activity;
	
	public static final int RESULT_UPDATE_DOG = 1;
	public static final int RESULT_PUSH_DOG_DATA = 2;
	public static final int RESULT_ADD_DOG = 3;
	
	public static final int RESULT_PUSH_CATEGORY_DATA = 4;
	public static final int RESULT_PULL_CATEGORY_DATA = 5;
	
	public static final int RESULT_PULL_USERS = 6;
	
	private ArrayList<Notifiable> observers = new ArrayList<Notifiable>();
	private int eventCode;
	
	private SyncManager(Context activity)
	{
		this.activity = activity;
	}
	public static SyncManager getInstance(Context activity)
	{
		if (instance == null && activity == null)
		{
			throw new IllegalArgumentException("Cannot give null activity when instance is not instantiated");
		}
		if (instance == null)
		{
			instance = new SyncManager(activity);
		}
		return instance;
	}
    public void syncCategoryDataWithServer(Context activity)
    {
    	this.activity = activity;
    	this.pushCategoryDataToServer();
    }
    private void pushCategoryDataToServer()
    {
    	ConnectionsManager cm = ConnectionsManager.getInstance(activity);
    	Log.i("TAG","Sending json");
    	TrainingInfoTether tether = TrainingInfoTether.getInstance();
    	JSONObject object = tether.getCategoryUpdateJSON(activity);
    	Log.i("TAG",object.toString());
    	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    	pairs.add(new BasicNameValuePair("jsonMessage", object.toString()));
    	cm.postToServer("updateTrainingInfo.php", pairs, this, SyncManager.RESULT_PUSH_CATEGORY_DATA);
    }
    private void pullCategoryDataFromServer()
    {

		DatabaseHandler db = new DatabaseHandler(activity);
		db.clearTable(DatabaseHandler.TABLE_SYNC);
		TrainingInfoTether tether = TrainingInfoTether.getInstance();
		JSONObject object = tether.getCategoryVersionNumbers(activity);
		ConnectionsManager cm = ConnectionsManager.getInstance(activity);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    	pairs.add(new BasicNameValuePair("jsonMessage", object.toString()));
    	cm.postToServer("getTrainingInfo.php", pairs, this, SyncManager.RESULT_PULL_CATEGORY_DATA); 
    }
    public void syncDogInfoWithServer(Context activity, Notifiable observer, int eventCode)
    {
    	if (observer != null)
    	{
	    	this.observers.add(observer);
	    	this.eventCode = eventCode;
    	}
    	
    	this.activity = activity;
    	ConnectionsManager cm = ConnectionsManager.getInstance(activity);
    	DogInfoTether tether = DogInfoTether.getInstance();
    	JSONObject idToVersionNumber = tether.getDogEntryVersionNumbers(activity);
    	//cm.pushJSONObjectToServer(this, "getDogs.php", idToVersionNumber, this, this.DOG_HAS_SYNCED);
    	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    	pairs.add(new BasicNameValuePair("idToVersionNumber", idToVersionNumber.toString()));
   	 	cm.postToServer("getDogs.php", pairs, this, SyncManager.RESULT_PUSH_DOG_DATA);
    }
    public void syncUsersWithServer(Context context, Notifiable observer, int eventCode)
    {
    	this.observers.add(observer);
    	this.eventCode = eventCode;
    	ConnectionsManager cm = ConnectionsManager.getInstance(context);
    	boolean isAvailable = cm.isGConnectionAvailable() || cm.isWifiAvailable();
    	if (!isAvailable) return;
    	cm.postToServer("getUsers.php", null, this, RESULT_PULL_USERS);
    }
    private void updateLocalWithDogInfo(String jsonMessage)
    {

		DogInfoTether tether = DogInfoTether.getInstance();
		tether.updateDogsWithJSON(jsonMessage, activity);
		// Notify the DogSelectorActivity to refresh it's list view
		if (observers.size() > 0)
		{
			for (Notifiable observer : this.observers)
			{
				observer.notifyOfEvent(eventCode, null);
			}
		}
		observers.clear();
    }
    private void updateLocalWithCategoryData(String jsonMessage)
    {
    	if (jsonMessage.equals("[]"))
    	{
    		Log.i("TAG","Up to date");
    		return;
    	}
    	JSONObject object = null;
    	try 
    	{
			object = new JSONObject(jsonMessage);
		} 
    	catch (JSONException e) 
		{
			e.printStackTrace();
		}
    	TrainingInfoTether tether = TrainingInfoTether.getInstance();
    	tether.updateDogTrainingInfo(object, activity);
    }
	@Override
	public void notifyOfEvent(int eventCode, String message)
	{
		ConnectionsManager cm = null;
		Log.i("TAG","MESSAGE: " + message);
		switch (eventCode)
		{
		case SyncManager.RESULT_UPDATE_DOG:
			//TODO: Implement
			break;
		case SyncManager.RESULT_PUSH_DOG_DATA:
			Log.i("TAG",message);
			cm = ConnectionsManager.getInstance(activity);
			if (!cm.isValidJSON(message))
			{
				ViewUtils utils = ViewUtils.getInstance();
				utils.showAlertMessage(activity, "Unable to connect to server.  Please try again later.");
				return;
			}
			this.updateLocalWithDogInfo(message);
			break;
		case SyncManager.RESULT_ADD_DOG:
			//TODO: Implement
			break;
		case SyncManager.RESULT_PUSH_CATEGORY_DATA:
			if (!message.equals("success"))
			{
				ViewUtils utils = ViewUtils.getInstance();
				utils.showAlertMessage(activity, "Unable to connect to server.  Please try again later.");
				return;
			}
			this.pullCategoryDataFromServer();
			break;
		case SyncManager.RESULT_PULL_CATEGORY_DATA:
			cm = ConnectionsManager.getInstance(activity);
			if (!cm.isValidJSON(message))
			{
				ViewUtils utils = ViewUtils.getInstance();
				utils.showAlertMessage(activity, "Unable to connect to server.  Please try again later.");
				return;
			}
			this.updateLocalWithCategoryData(message);
			break;
		case SyncManager.RESULT_PULL_USERS:
			Log.i("TAG","Sending to LogInActivity");
			for (Notifiable observer : this.observers)
			{
				observer.notifyOfEvent(this.eventCode, message);
			}
			this.observers.clear();
			break;
		}
		
	}
	
	
}
