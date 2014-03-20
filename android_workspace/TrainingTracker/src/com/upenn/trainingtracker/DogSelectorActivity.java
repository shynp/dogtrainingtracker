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
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DogSelectorActivity extends FragmentActivity implements Notifiable
{
	/**
	 * These are the result-codes that identify which activity the user is returning from.  All of these
	 * activities are started from the ImageSelectorImageView of the add_dog_layout.
	 */
	public final static int CAMERA_INTENT_RESULT_CODE = 1; // Returning from camera app
	public final static int CROP_INTENT_RESULT_CODE = 2;   // Returning from cropping after camera app
	public final static int GALLERY_INTENT_RESULT_CODE = 3; // Retruning from gallery
	public final static int CROP_INTENT_RESULT_CODE_FROM_GALLERY = 4; // Returning from cropping after gallery
	
	public final int DOG_HAS_SYNCED = 5;
	private ArrayList<DogProfile> profiles;
	private Dialog addDogDialog;

	private ImageSelectorImageView imageSelector;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dog_selector_layout);
		DatabaseHandler handler = new DatabaseHandler(this);
   	 	profiles = handler.getDogProfiles(this);
   	 	
		final LazyAdapter adapter = new LazyAdapter(this, profiles);
		ListView list = (ListView) this.findViewById(R.id.list);
		list.setAdapter(adapter);
		Log.i("TAG","Calling Filter");
		adapter.getFilter().filter("");
		
        EditText filterEditText = (EditText) findViewById(R.id.dogFilterTextID);
        
        // Add Text Change Listener to EditText
        filterEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
            	Log.i("TAG","Filtering");
                adapter.getFilter().filter(s.toString().trim());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
		//this.renderProfileWidgets();
	}
	public void refreshListDisplay()
	{
		DatabaseHandler handler = new DatabaseHandler(this);
   	 	profiles = handler.getDogProfiles(this);
   	 	
		final LazyAdapter adapter = new LazyAdapter(this, profiles);
		ListView list = (ListView) this.findViewById(R.id.list);
		list.setAdapter(adapter);
	}
	/**
	 * The imageSelector (of type ImageSelectorImageView) launches the CropImage activity (in janmuller package)
	 * It launches it for a result (see "startActivityForResult").  The result is returned to the current activity
	 * which is this class.  The requestCode which was set when the activity was launched is now used to determine 
	 * the appropriate action to take.
	 */
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
	/**
	 * This method is overridden so that the menu can be set.  The menu is opened when the physical
	 * "menu" button is pressed on the android
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dog_selector_menu, menu);
        return true;
    }
    /**
     * This method is called by the add dog button (as defined in add_dog_layout.xml
     * Both the date-selector and image-selector need a reference to the parent activity. So, these elements
     * are retrieved via their id and passed a reference to this actiivty
     * @param view
     */
    public void openAddDogPopUp(final View view)
    {
    	ConnectionsManager cm = ConnectionsManager.getInstance(this);
    	boolean isEnabled = cm.checkForWifi(this, "Wifi is needed to add a new dog");
    	if (!isEnabled) return;
    	
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
    /**
     * This method is called when the user submits the new dog entry.  Values are retrieved from the fields
     * and then the values are pushed to the server with an AsyncTask
     * @param view
     */
    public void addNewDogEntry(final View view)
    {
    	ConnectionsManager cm = ConnectionsManager.getInstance(this);
    	boolean isEnabled = cm.checkForWifi(this, "Wifi is needed to add a new dog");
    	if (!isEnabled) return;
    	
    	String name = ((TextView)this.addDogDialog.findViewById(R.id.nameID)).getText().toString().trim();
    	String breed = ((TextView)this.addDogDialog.findViewById(R.id.breedID)).getText().toString().trim();
    	String serviceType = ((TextView)this.addDogDialog.findViewById(R.id.serviceTypeID)).getText().toString().trim();
    	
    	DateSelectorTextView dateSelector = (DateSelectorTextView) this.addDogDialog.findViewById(R.id.dateSelectorTextViewID);
    	Calendar dob = dateSelector.getDateOfBirth();
    	
    	boolean shouldContinue = this.validateNewDogData(dob, name, breed, serviceType);
    	if (!shouldContinue) return;
    	
    	String dobString = dob.get(Calendar.YEAR) + "-" + dob.get(Calendar.MONTH) + "-" + dob.get(Calendar.DAY_OF_MONTH);
    	
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
    	this.addDogDialog.cancel();
    	this.syncWithServer();
    	//TODO: Close the dialog.  Currently left open for the purpose of debugging
    }
    

    /**
     * Checks to ensure all new dog data is valid
     * @param dob
     * @param name
     * @param breed
     * @param serviceType
     * @return true or false depending on if program should continue adding the dog info to server
     */
    public boolean validateNewDogData(Calendar dob, String name, String breed, String serviceType)
    {
    	boolean allFieldsFull = !name.equals("") && !breed.equals("") && ! serviceType.equals("") && dob != null;
    	if (!allFieldsFull) Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
    	return allFieldsFull;
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
        	 this.syncWithServer();
         default:
             return super.onOptionsItemSelected(item);
         }
    }
    public void syncWithServer()
    {
    	ConnectionsManager cm = ConnectionsManager.getInstance(this);
    	cm.pullDogsFromServer(this, this, this.DOG_HAS_SYNCED);
    	//TODO: UPDATE
    	//this.renderProfileWidgets();
    }
	@Override
	public void notifyOfEvent(int eventCode) 
	{
		if (eventCode == this.DOG_HAS_SYNCED)
		{
	    	this.refreshListDisplay();
		}
	}


}
