package com.upenn.trainingtracker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.upenn.trainingtracker.customviews.DateSelectorTextView;
import com.upenn.trainingtracker.customviews.ImageSelectorImageView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DogSelectorActivity extends FragmentActivity
{
	public final static int CAMERA_INTENT_RESULT_CODE = 1;
	public final static int CROP_INTENT_RESULT_CODE = 2;
	public final static int GALLERY_INTENT_RESULT_CODE = 3;
	public final static int CROP_INTENT_RESULT_CODE_FROM_GALLERY = 4;
	
	private Dialog addDogDialog;

	private ImageSelectorImageView imageSelector;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dog_selector_layout);
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.i("TAG","A RESULT HAS: " + requestCode);
		if (requestCode == DogSelectorActivity.CAMERA_INTENT_RESULT_CODE)
		{
			Log.i("TAG", "d");
			imageSelector.cropCameraResult();
		}
		else if (requestCode == DogSelectorActivity.CROP_INTENT_RESULT_CODE)
		{
			imageSelector.setCropResult();
		}
		else if (requestCode == DogSelectorActivity.GALLERY_INTENT_RESULT_CODE)
		{
			imageSelector.cropGalleryResult(data);
		}
		else if (requestCode == DogSelectorActivity.CROP_INTENT_RESULT_CODE_FROM_GALLERY)
		{
			imageSelector.setCropGalleryResult();
		}
	}
	   // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dog_selector_menu, menu);
        return true;
    }
    public void openDateSelector()
    {
	
    }
    public void openAddDogPopUp(final View view)
    {
    	this.addDogDialog = new Dialog(this);
    	addDogDialog.setTitle("Add New Dog");
    	addDogDialog.setContentView(R.layout.add_dog_layout);
    	DateSelectorTextView dateSelector = (DateSelectorTextView) addDogDialog.findViewById(R.id.dateSelectorTextViewID); 
    	dateSelector.setParentFragment(this);
    	imageSelector = (ImageSelectorImageView) addDogDialog.findViewById(R.id.dogImageID); 
    	imageSelector.setParentActivity(this);

    	// Set behavior of add-dog button
    	Button addDogButton = (Button) this.addDogDialog.findViewById(R.id.addNewDogButtonID);
    	addDogButton.setOnClickListener(new OnClickListener()
    	{
			@Override
			public void onClick(View view) 
			{
				DogSelectorActivity.this.addNewDogEntry(view);
			}    		
    	});
    	addDogDialog.show();

    }
    public void addNewDogEntry(final View view)
    {
    	String name = ((TextView)this.addDogDialog.findViewById(R.id.nameID)).getText().toString().trim();
    	String breed = ((TextView)this.addDogDialog.findViewById(R.id.breedID)).getText().toString().trim();
    	String serviceType = ((TextView)this.addDogDialog.findViewById(R.id.serviceTypeID)).getText().toString().trim();
    	
    	DateSelectorTextView dateSelector = (DateSelectorTextView) this.addDogDialog.findViewById(R.id.dateSelectorTextViewID);
    	Calendar dob = dateSelector.getDateOfBirth();
    	String dobString = dob.get(Calendar.DAY_OF_MONTH) + "-" + dob.get(Calendar.MONTH) + "-" + dob.get(Calendar.YEAR);

    	// Get image and encode as string
    	ImageSelectorImageView imageSelector = (ImageSelectorImageView) this.addDogDialog.findViewById(R.id.dogImageID);
    	Bitmap image = imageSelector.getBitmap();
    	ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    	image.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
    	byte[] byteArray = outStream.toByteArray();
		String byteString = Base64.encodeToString(byteArray, 0);
    	
		// Add pairs that will be sent via HTTP 
    	final List<NameValuePair> pairs = new ArrayList<NameValuePair>();

    	pairs.add(new BasicNameValuePair("name", name));
    	pairs.add(new BasicNameValuePair("breed", breed));
    	pairs.add(new BasicNameValuePair("serviceType", serviceType));
    	pairs.add(new BasicNameValuePair("dob", dobString));
    	pairs.add(new BasicNameValuePair("imageString", byteString));
    	
    	
    	new AsyncTask<String, String, String>() {
    		@Override
    		protected String doInBackground(String... params) 
    		{
    			try
    			{ 	
    				HttpClient httpClient = new DefaultHttpClient();
    				HttpPost httpPost = new HttpPost(Keys.SITE + "addDog.php");
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
    			// TODO: Update the local database
    		}
    	}.execute(null,null,null);
    }
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	 switch (item.getItemId())
         {
         case R.id.itemSyncID:
        	 ConnectionsManager cm = ConnectionsManager.getInstance(this);
        	 cm.pullDogsFromServer(this);
         default:
             return super.onOptionsItemSelected(item);
         }
    }
}
