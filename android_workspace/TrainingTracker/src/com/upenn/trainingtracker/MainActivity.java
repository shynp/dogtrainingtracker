package com.upenn.trainingtracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class MainActivity extends Activity {

	public static final String USER_PREFS = "user_prefs";
	public static final String USER_NAME_KEY = "user_name";
	public static final String NAME_KEY = "real_name";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.checkLogInStatus();

    }
    private void checkLogInStatus()
    {
        SharedPreferences preferences = this.getSharedPreferences(MainActivity.USER_PREFS, 0);
        if (preferences.contains(MainActivity.USER_NAME_KEY))
        {
        	// Change to DogSelectionActivity and display welcome message
        }
        else
        {
        	// Start LogInActivity
        	Intent intent = new Intent(this, LogInActivity.class);
        	this.startActivity(intent);
        }
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
