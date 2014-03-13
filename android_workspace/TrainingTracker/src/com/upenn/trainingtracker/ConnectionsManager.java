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
import android.content.Context;
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
	public boolean isWifiAvailable()
	{
		ConnectivityManager connManager = (ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}
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
	public void pullDogsFromServer(final Activity activity)
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
    			//DatabaseHandler handler = new DatabaseHandler(activity.getApplicationContext());
    			//handler.updateUsersWithJSON(result);
    		}
    	}.execute(null,null,null);
	}
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
    public static void pushValueToServer()
    {
    	
    }

}
