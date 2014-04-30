package com.upenn.trainingtracker;

import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
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
    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.upenn.trainingtracker.StubProvider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.upenn.trainingtracker";
    // The account name
    public static final String ACCOUNT = "AdminSyncAccount";
    // Instance fields
    Account mAccount;
    
    // Sync interval constants
    public static final long SYNC_INTERVAL_IN_SECONDS = (60 * 15);
   private ContentResolver mResolver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.checkLogInStatus();
        this.mAccount = this.CreateSyncAccount(this);
        this.setUpAutoSync();
        ContentResolver.requestSync(
        		  mAccount,ACCOUNT_TYPE, Bundle.EMPTY);
    }
    public void setUpAutoSync()
    {
        // Get the content resolver for your app
        mResolver = getContentResolver();
        /*
         * Turn on periodic syncing
         */
        Log.i("TAG","Passing null bundle");
        ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);

        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL_IN_SECONDS);

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
    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
        	ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
        	ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
        	Log.i("TAG","AN ERROR OCCURED");
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }
    
}
