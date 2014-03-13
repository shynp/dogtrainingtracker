package com.upenn.trainingtracker;

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
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends Activity
{
	private Dialog dialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
     //   DatabaseHandler dbHandler = new DatabaseHandler(this);
        ConnectionsManager cm = ConnectionsManager.getInstance(this);
        this.setContentView(R.layout.log_in_layout);
        if (cm.isWifiAvailable())
        {
        	//dbHandler.syncUsersAndDogs();
        }
    }
    private void addNewUserToServer(String name, String username, String password,
    		String email, String phone)
    {

    	final List<NameValuePair> pairs = new ArrayList<NameValuePair>();

    	pairs.add(new BasicNameValuePair("name", name));
    	pairs.add(new BasicNameValuePair("username", username));
    	pairs.add(new BasicNameValuePair("password", password));
    	pairs.add(new BasicNameValuePair("email", email));
    	pairs.add(new BasicNameValuePair("phone", phone));

    	new AsyncTask<String, String, String>() {
    		@Override
    		protected String doInBackground(String... params) 
    		{
    			try
    			{
    				HttpClient httpClient = new DefaultHttpClient();
    				HttpPost httpPost = new HttpPost(Keys.SITE + "addUser.php");
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
    			if (result.equals("invalid_username"))
    			{
    				Toast.makeText(LogInActivity.this, "Username has already been taken", Toast.LENGTH_LONG).show();
    			}
    			else
    			{
    				dialog.cancel();
    			}
    		}
    	}.execute(null,null,null);

    }

    public void logInCallBack(final View view)
    {
    	EditText usernameView = (EditText) this.findViewById(R.id.username);
    	EditText passwordView = (EditText) this.findViewById(R.id.password);
    	String userName = usernameView.getText().toString();
    	String password = passwordView.getText().toString();
    	
    	DatabaseHandler db = new DatabaseHandler(this);
    	if (db.isValidUser(userName, password))
    	{
    		//Toast.makeText(this, "Valid Credentials", Toast.LENGTH_LONG).show();
    		Intent intent = new Intent(this, DogSelectorActivity.class);
    		LogInActivity.this.startActivity(intent);
    	}
    	else
    	{
    		Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG).show();
    	}
    }
    public void openRecoverAccountPopup(final View view)
    {
    	dialog = new Dialog(this);
    	dialog.setContentView(R.layout.recover_account_layout);
    	dialog.setTitle("Account Recovery");
    	Button createAccountButton = (Button) dialog.findViewById(R.id.createAccountButtonID);
    	createAccountButton.setOnClickListener(new OnClickListener()
    	{
			@Override
			public void onClick(View buttonView) 
			{
		    	EditText emailET = (EditText) dialog.findViewById(R.id.emailID);
		    	String email = emailET.getText().toString();
		    	Log.i("TAG",email);
				ConnectionsManager cm = ConnectionsManager.getInstance(LogInActivity.this);
				cm.promptRecoveryEmail(LogInActivity.this, email);
				dialog.dismiss();
			}
    	});
    	dialog.show();
    }
    public void openCreateAccountPopup(final View view)
    {
    	dialog = new Dialog(this);
    	dialog.setContentView(R.layout.create_account_layout);
    	dialog.setTitle("Account Creation");
    	Button createAccountButton = (Button) dialog.findViewById(R.id.createAccountButtonID);
    	createAccountButton.setOnClickListener(new OnClickListener()
    	{
			@Override
			public void onClick(View buttonView) 
			{
			   	EditText fullNameET = (EditText) dialog.findViewById(R.id.nameID);
		    	EditText userNameET = (EditText) dialog.findViewById(R.id.userNmeID);
		    	EditText passwordET = (EditText) dialog.findViewById(R.id.passwordID);
		    	EditText emailET = (EditText) dialog.findViewById(R.id.emailID);
		    	EditText phoneET = (EditText) dialog.findViewById(R.id.phoneID);
		    	
		    	String fullName = fullNameET.getText().toString().trim();
		    	String userName = userNameET.getText().toString().trim();
		    	String password = passwordET.getText().toString().trim();
		    	String email = emailET.getText().toString().trim();
		    	String phone = phoneET.getText().toString().trim();
		    	
		    	if (fullName.equals("") || userName.equals("") || password.equals("") || email.equals("") ||
		    			phone.equals(""))
		    	{
		    		Toast.makeText(LogInActivity.this, "Empty Field(s)", Toast.LENGTH_LONG).show();
		    		return;
		    	}
		    	Log.i("TAG", "New Account: " + fullName + " " + userName + " " + password + " " + email + " " + phone);
		    	LogInActivity.this.addNewUserToServer(fullName, userName, password, email, phone);
		    	ConnectionsManager cm = ConnectionsManager.getInstance(LogInActivity.this);
		    	cm.pullUsersFromServer(LogInActivity.this);
			}
    	});
    	dialog.show();
    }
}
