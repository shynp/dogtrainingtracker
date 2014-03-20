package com.upenn.trainingtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ConnectionsManager 
{
	private Activity activity;
	private static ConnectionsManager instance;
	
	private ConnectionsManager(Activity activity)
	{
		this.activity = activity;
	}
	public static ConnectionsManager getInstance(Activity activity)
	{
		if (instance == null)
		{
			instance = new ConnectionsManager(activity);
		}
		return instance;
	}
	/**
	 * Checks to see if wifi is enabled and available
	 * @return
	 */
	public boolean isWifiAvailable()
	{
		ConnectivityManager connManager = (ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
	/**
	 * Sends notification to the server to send a recovery email if the provided email is valid
	 * @param activity
	 * @param email
	 */
	public void promptRecoveryEmail(final Activity activity, String email)
	{
    	final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    	pairs.add(new BasicNameValuePair("validation", Keys.CONNECTION_PASSWORD));
    	pairs.add(new BasicNameValuePair("email", email));
    	
    	new AsyncTask<String, String, String>() {
    		@Override
    		protected String doInBackground(String... params) 
    		{
    			try
    			{
    				HttpClient httpClient = new DefaultHttpClient();
    				HttpPost httpPost = new HttpPost(Keys.SITE + "recoverAccount.php");
    				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
    				HttpResponse response = httpClient.execute(httpPost);
    				HttpEntity entity = response.getEntity();
    				String result = ConnectionsManager.inputStreamToString(entity.getContent()).toString();
    				Log.i("TAG",result);
    				return result;
    			}
    			catch (Exception e)
    			{
    				e.printStackTrace();
    			}
    			return "";
    		}
    		@Override
    		protected void onPostExecute(String result)
    		{
    			if (result.equals("invalid_id"))
    			{
    				Log.i("TAG","Invalid id");
    				return;
    			}
    			else if (result.equals("invalid_email"))
    			{
    				Toast.makeText(activity, "Invalid email address", Toast.LENGTH_LONG).show();
    			}
    			else
    			{
    				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    				builder.setTitle("Account Recovery");
    				builder.setMessage("A recovery email has been sent to your email.");
    				builder.setPositiveButton("Ok",null);
    				builder.create().show();
    			}
    		}
    	}.execute(null,null,null);
	}
	public void openWifiSettings(Activity activity)
	{
		  final Intent intent = new Intent(Intent.ACTION_MAIN, null);
          intent.addCategory(Intent.CATEGORY_LAUNCHER);
          final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
          intent.setComponent(cn);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          activity.startActivity( intent);
	}
	/**
	 * Checks to see if wifi is enabled.  If it is not shows the given error message
	 * @param activity
	 * @param errorMessage
	 */
	public boolean checkForWifi(final Activity activity, String errorMessage)
	{
    	if (!this.isWifiAvailable())
    	{
    		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    		builder.setMessage(errorMessage);
    		builder.setNegativeButton("Select Network", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) 
				{
					ConnectionsManager.this.openWifiSettings(activity);
				}
    		});
    		builder.setPositiveButton("Cancel", null);
    		builder.create().show();
    		return false;
    	}
    	return true;
	}
	/**
	 * Pull the users from server and afterwards update the local database copy
	 * @param activity
	 */
	public void pullUsersFromServer(final Activity activity)
	{
    	final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    	pairs.add(new BasicNameValuePair("validation", Keys.CONNECTION_PASSWORD));
    	
    	new AsyncTask<String, String, String>() {
    		@Override
    		protected String doInBackground(String... params) 
    		{
    			try
    			{
    				HttpClient httpClient = new DefaultHttpClient();
    				HttpPost httpPost = new HttpPost(Keys.SITE + "getUsers.php");
    				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
    				HttpResponse response = httpClient.execute(httpPost);
    				HttpEntity entity = response.getEntity();
    				String result = ConnectionsManager.inputStreamToString(entity.getContent()).toString();
    				Log.i("TAG",result);
    				return result;
    			}
    			catch (Exception e)
    			{
    				e.printStackTrace();
    			}
    			return "";
    		}
    		@Override
    		protected void onPostExecute(String result)
    		{
    			if (result.equals("invalid_id"))
    			{
    				Log.i("TAG","Invalid id");
    				return;
    			}
    			Log.i("TAG","Creating database handler");
    			DatabaseHandler handler = new DatabaseHandler(activity.getApplicationContext());
    			handler.updateUsersWithJSON(result);
    		}
    	}.execute(null,null,null);
	}
	/**
	 * Pull the dog information from the server and afterwards update the local copy.
	 * This does not update the individual training data only the basic information for each dog
	 * such as name, picture, category, etc.
	 * @param activity
	 */
	public void pullDogsFromServer(final Activity activity, final Notifiable notifier, final int eventCode)
	{
    	final List<NameValuePair> pairs = new ArrayList<NameValuePair>();
    	pairs.add(new BasicNameValuePair("validation", Keys.CONNECTION_PASSWORD));
    	
    	new AsyncTask<String, String, String>() {
    		@Override
    		protected String doInBackground(String... params) 
    		{
    			try
    			{
    				HttpClient httpClient = new DefaultHttpClient();
    				HttpPost httpPost = new HttpPost(Keys.SITE + "getDogs.php");
    				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
    				HttpResponse response = httpClient.execute(httpPost);
    				HttpEntity entity = response.getEntity();
    				String result = ConnectionsManager.inputStreamToString(entity.getContent()).toString();
    				Log.i("QUERY RESULT",result);
    				return result;
    			}
    			catch (Exception e)
    			{
    				e.printStackTrace();
    			}
    			return "";
    		}
    		@Override
    		protected void onPostExecute(String result)
    		{
    			if (result.equals("invalid_id"))
    			{
    				Log.i("TAG","Invalid id");
    				return;
    			}
    			Log.i("TAG","Creating database handler");
    			DatabaseHandler handler = new DatabaseHandler(activity.getApplicationContext());
    			handler.updateDogsWithJSON(result, activity);
    			if (notifier != null)
    			{
    				notifier.notifyOfEvent(eventCode);
    			}
    		}
    	}.execute(null,null,null);
	}
	/**
	 * Takes the input stream returned from the HTTP request and returns a StringBuidler.  This can
	 * then be converted to a string.
	 * @param is
	 * @return
	 */
    public static StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try 
        {
	        while ((rLine = rd.readLine()) != null) 
	        {
	        	 answer.append(rLine);
	        }
        }  
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return answer;
    }

}
