package com.upenn.trainingtracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity 
{
	// THIS IS THE FIRST ACTIVITY TO BE RUN
	
	
	/*
	 * User preferences is a key-value storage for small amounts of data.
	 * The name of the user-preferences is given by USER_PREFS and it currently
	 * stores two values denoted by the keys USER_NAME_KEY and NAME_KEY
	 */
	public static final String USER_PREFS = "user_prefs";
	public static final String USER_NAME_KEY = "user_name";
	public static final String USER_PASSWORD_KEY = "user_password";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.checkLogInStatus();
    }
    /**
     * If the shared preferences contains login credentials open the DogSelectorActivity otherwise
     * route the user to the LogInActivity
     */
    private void checkLogInStatus()
    {
    	Log.i("TAG","Checking");
        SharedPreferences preferences = this.getSharedPreferences(MainActivity.USER_PREFS, 0);
        if (preferences.contains(MainActivity.USER_NAME_KEY))
        {
        	Log.i("TAG","Checking preferences");
        	DatabaseHandler db = new DatabaseHandler(this);
        	if (db.isValidUser(preferences.getString(USER_NAME_KEY, ""), preferences.getString(USER_PASSWORD_KEY,"")))
        	{
        		Intent intent = new Intent(this, DogSelectorActivity.class);
        		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        		intent.putExtra(MainActivity.USER_NAME_KEY, preferences.getString(USER_NAME_KEY, ""));
        		this.startActivity(intent);
        		return;
        	}
        }
        	// Start LogInActivity
        Intent intent = new Intent(this, LogInActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
        
    }

    /**
     * Specifies the options that will be presented when the physical "menu" button is pressed on the android
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
