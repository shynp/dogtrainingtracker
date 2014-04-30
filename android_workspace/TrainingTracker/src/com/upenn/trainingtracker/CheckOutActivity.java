package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.upenn.trainingtracker.customviews.AutoCategorySelector;
import com.upenn.trainingtracker.customviews.CheckOutProgressView;
import com.upenn.trainingtracker.customviews.PlanningBinLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class CheckOutActivity extends Activity
{
	Map<String, List<PlanEntry>> catKeyToPlanEntries;
	Map<String, PlanningBinLayout> catKeyToView;
	
	Map<PlanEntry, View> planEntryToView;
	
	
	private String currentCategory;
	private List<String> catKeys;
	private GestureDetector gestureDetector;
	private CheckOutActivity.MyGestureDetector gestureDetectorInner;
	private int dogID;
	private CheckOutProgressView progressView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.check_out_layout);
		View parentView = this.findViewById(R.id.checkOutParentView);
		this.attachSwipeListener(parentView);
		Bundle extras = this.getIntent().getExtras();
		
		String[] categories = extras.getStringArray("categoryKeys");
		this.catKeys = Arrays.asList(categories);
		this.initializeCheckOutProgressView(categories.length);
		this.planEntryToView = new HashMap<PlanEntry, View>();
		this.dogID = extras.getInt("dogID");
		this.currentCategory = categories[0];
		gestureDetectorInner = new MyGestureDetector();
		gestureDetector = new GestureDetector(this, gestureDetectorInner);
		this.initializeLayout(categories);
	}
	private void initializeCheckOutProgressView(int numViews)
	{
		progressView = (CheckOutProgressView) this.findViewById(R.id.checkOutProgressView);
		progressView.initializeCheckOutProgressView(numViews);
	}
	private void initializeLayout(String[] catKeys)
	{
		Log.i("TAG","b");
		String[] categories = new String[catKeys.length];
		Log.i("TAG","a");
		final TrainingReader reader = TrainingReader.getInstance(this);
		for (int index = 0; index < categories.length; ++index)
		{
			categories[index] = reader.catKeyToCategory(catKeys[index]);
		}
		Spinner spinner = (Spinner) this.findViewById(R.id.categorySelectorID);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				String category = parent.getItemAtPosition(position).toString();
				String catKey = reader.categoryToCatKey(category);
				CheckOutActivity.this.switchToViewByCategory(catKey);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		this.catKeyToPlanEntries = new HashMap<String, List<PlanEntry>>();
		this.catKeyToView = new HashMap<String, PlanningBinLayout>();
		// Initialize category to entries structure and category to view structure
		for (String catKey : catKeys)
		{
			List<PlanEntry> entries = reader.getViewCompositionListByCategoryKey(catKey);
			this.catKeyToPlanEntries.put(catKey, entries);
			this.catKeyToView.put(catKey, this.getViewForCategory(catKey));
		}
		// Set view to current view
		this.switchToViewByCategory(this.currentCategory);
		Log.i("TAG","eeee");
	}
	public void recordPlanInformation()
	{
		TrainingInfoTether tether = TrainingInfoTether.getInstance();
		for (String cat : this.catKeys)
		{
			PlanningBinLayout binLayout = this.catKeyToView.get(cat);
			List<PlanEntry> entries = this.catKeyToPlanEntries.get(cat);
			
			String plan = "";
			for (int index = 0; index < entries.size(); ++index)
			{
				PlanEntry entry = entries.get(index);
				String value = "";
				if (entry.getType() == PlanEntry.Type.CHECKBOX)
				{
					value = binLayout.getCheckBoxValueByCategory(entry.getNameKey());
				}
				else
				{
					value = binLayout.getSpinnerValueByCategory(entry.getNameKey());
				}
				plan += entry.getNameKey() + "==" + value;
				if (index != entries.size() - 1)
				{
					plan += "||";
				}
			}
			Calendar c = Calendar.getInstance(); 
			int month = c.get(Calendar.MONTH) + 1;
			int day = c.get(Calendar.DAY_OF_MONTH);
			int year = c.get(Calendar.YEAR);
			
			int hours = c.get(Calendar.HOUR_OF_DAY);
			int minutes = c.get(Calendar.MINUTE);
			int second = c.get(Calendar.SECOND);
			String dateString = month + "-" + day + "-" + year + "-" + hours + ":" + minutes + ":" + second;
			
			SharedPreferences preferences = this.getSharedPreferences(MainActivity.USER_PREFS, 0);
			String userName = preferences.getString(MainActivity.USER_NAME_KEY, "");
			tether.addPlan(dateString, plan, cat, dogID, userName, this);
			
			Log.i("TAG","Plan for " + cat + ": " + plan);
		}
	}
	/**
	 * Called via the "Press Values" button.  It looks for other subcategories within other parent views that have
	 * the same structure (meaning the same nameValue as well as the same optionValues for a spinner). If a match
	 * is found then the value is set to be the same.  This will help eliminate duplication of effort on the part 
	 * of the user for identical fields
	 * @param view
	 */
	public void pressValues(final View view)
	{
		List<PlanEntry> entries = this.catKeyToPlanEntries.get(this.currentCategory);
		
		for (PlanEntry entry : entries)
		{
			for (String category : this.catKeys)
			{
				if (category.equals(this.currentCategory)) continue;
				
				List<PlanEntry> otherEntries = this.catKeyToPlanEntries.get(category);
				for (PlanEntry otherEntry : otherEntries)
				{
					if (otherEntry.equals(entry))
					{
						Log.i("TAG","Found equal plan entries!!!!");
						if (entry.getType() == PlanEntry.Type.CHECKBOX)
						{
							Log.i("TAG","Changing checkbox");
							CheckBox check = (CheckBox) this.planEntryToView.get(entry);
							CheckBox checkOther = (CheckBox) this.planEntryToView.get(otherEntry);
							checkOther.setChecked(check.isChecked());
						}
						else if (entry.getType() == PlanEntry.Type.OPTIONS)
						{
							Spinner spinner = (Spinner) this.planEntryToView.get(entry);
							Spinner spinnerOther = (Spinner) this.planEntryToView.get(otherEntry);
							String value = (String) spinner.getSelectedItem();
							
							ArrayAdapter<String> adapter = ((ArrayAdapter<String>)spinnerOther.getAdapter());
							int position = adapter.getPosition(value);
							spinnerOther.setSelection(position);
						}
					}
				}
			}
		}
	}
	public void submitPlan(final View view)
	{
		Log.i("TAG","Submiting info");
		this.recordPlanInformation();
		
/*		String[] catKeys = new String[this.catKeys.size()];
		TrainingReader reader = TrainingReader.getInstance(this);
		for (int index = 0; index < catKeys.length; ++index)
		{
			String catKey = reader.categoryToCatKey(this.catKeys.get(index));
			catKeys[index] = catKey;
		}*/
		String[] catKeysArray = new String[catKeys.size()];
		for (int index = 0; index < this.catKeys.size(); ++index)
		{
			catKeysArray[index] = this.catKeys.get(index);
		}
		/*Intent intent = new Intent(CheckOutActivity.this, SessionActivity.class);
		intent.putExtra("categoryKeys", catKeysArray);
		intent.putExtra("dogID", this.dogID);
		this.finish();
		this.startActivity(intent); */
		Log.i("TAG","Sending result");
		setResult(RESULT_OK,null);  
		this.finish();
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) 
	    {
	    	setResult(RESULT_CANCELED, null);
	    }
	    return super.onKeyDown(keyCode, event);
	}
	/*
	 * Methods for rendering the views are below.  Each of the views are first created.  The parent view is a custom
	 * view of the PlanningBinLayout class.  For example, if you selected the categories of Patterns, Over, and Tunnel
	 * then three of these parent views would be created and stored in the categoryToView map (instance variable).
	 * When a sub-category is added, the CheckBox or Spinner is registered with the parent view with the sub-category key
	 * as the identifier.  This allows for efficient harvesting of the information.  The spinners display the options
	 * but we want to harvest the option-keys.  For this reason, both the options and option-keys are given to the 
	 * parent layout.  This way it can translate the selected value to the key value
	 */
	private void switchToViewByCategory(String catKey)
	{
		LinearLayout parentLayout = (LinearLayout) this.findViewById(R.id.checkOutScrollBin);
		parentLayout.removeAllViews();
		View view = this.catKeyToView.get(catKey);
		this.currentCategory = catKey;
		parentLayout.addView(view);
		int catIndex = this.catKeys.indexOf(catKey);
		this.progressView.setSelected(catIndex);
	}
	private PlanningBinLayout getViewForCategory(String catKey)
	{
		TrainingInfoTether tether = TrainingInfoTether.getInstance();
    	
		Map<String, String> plan = tether.getPlan(catKey, this.dogID, this);
		
		Log.i("TAG","------------Getting view for category: " + catKey);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		PlanningBinLayout viewBinParent = (PlanningBinLayout) inflater.inflate(R.layout.check_out_view_bin, null);
		TableLayout viewBin = (TableLayout) viewBinParent.findViewById(R.id.tableLayoutBin);

		for (PlanEntry entry: this.catKeyToPlanEntries.get(catKey))
		{
			Log.i("TAG","::: Plan Entry: " + entry.getName());
			TableRow canvasLayout = null;
			switch (entry.getType())
			{
				case CHECKBOX: 
					canvasLayout = (TableRow) this.getCheckBoxFromEntry(entry, viewBinParent);
					if (plan != null)
					{
						viewBinParent.setCheckBoxByKeys(entry.getNameKey(), plan.get(entry.getNameKey()));
					}
				break;
				case OPTIONS: 
					canvasLayout = (TableRow) this.getSpinnerFromEntry(entry, viewBinParent);
					if (plan != null)
					{
						viewBinParent.setSpinnerByKeys(entry.getNameKey(), plan.get(entry.getNameKey()));
						Log.i("TAG","changing");
					}
					else
					{
						Log.i("TAG", "null");
					}
				break;
				case IMAGE_OPTIONS:// view = this.getImageSpinnerFromEntry(entry);
					canvasLayout = (TableRow) this.getImageSpinnerFromEntry(entry, viewBinParent);
					if (plan != null)
					{
						viewBinParent.setSpinnerByKeys(entry.getNameKey(), plan.get(entry.getNameKey()));
						Log.i("TAG","changing");
					}
					else
					{
						Log.i("TAG", "null");
					}
				break;
			}
			viewBin.addView(canvasLayout);
		}
		
		this.attachSwipeListener(viewBin);
		return viewBinParent;
	}

	private LinearLayout getCheckBoxFromEntry(PlanEntry entry, PlanningBinLayout viewBinParent)
	{
		Log.i("TAG","Adding checkbox");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout checkLayout = (LinearLayout) inflater.inflate(R.layout.options_check_widget, null);
		
		CheckBox check = (CheckBox) checkLayout.findViewById(R.id.optionsCheckWidget);
		viewBinParent.registerCheckBox(entry.getNameKey(), check);
		
		this.planEntryToView.put(entry, check);
		
		TextView text = (TextView) checkLayout.findViewById(R.id.optionsCheckWidgetText);
		text.setText(entry.getName());		
		
		return checkLayout;
	}
	private LinearLayout getSpinnerFromEntry(PlanEntry entry, PlanningBinLayout viewBinParent)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.options_widget, null);
		
		TextView text = (TextView) layout.findViewById(R.id.optionsTextID);
		text.setText(entry.getName());
		
		Spinner spinner = (Spinner) layout.findViewById(R.id.optionsSpinnerID);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, entry.getOptions());
		spinner.setAdapter(spinnerArrayAdapter);
		viewBinParent.registerSpinner(entry.getNameKey(), entry.getOptionKeys(), spinner);
		
		this.planEntryToView.put(entry, spinner);
		
		return layout;
	}
	private LinearLayout getImageSpinnerFromEntry(PlanEntry entry, PlanningBinLayout viewBinParent)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.options_widget, null);
		
		TextView text = (TextView) layout.findViewById(R.id.optionsTextID);
		text.setText(entry.getName());
		
		ImageManager provider = ImageManager.getInstance();
		String[] keys = entry.getOptionKeys();
		Integer[] drawables = new Integer[entry.getOptionKeys().length];
		for (int index = 0; index < keys.length; ++index)
		{
			Log.i("TAG","trying to get drawable: " + keys[index]);
			drawables[index] = provider.keyToDrawableID(keys[index]);
		}
		Log.i("TAG","done getting drawables");
		
		Spinner spinner = (Spinner) layout.findViewById(R.id.optionsSpinnerID);
		ImageAdapter adapter = new ImageAdapter(this, drawables);
		spinner.setAdapter(adapter);
		viewBinParent.registerSpinner(entry.getNameKey(), entry.getOptionKeys(), spinner);
		
		this.planEntryToView.put(entry, spinner);
		
		return layout;
	}

	private void setSpinner(String catKey)
	{
		TrainingReader reader = TrainingReader.getInstance(this);
		String category = reader.catKeyToCategory(catKey);
		Spinner spinner = (Spinner) this.findViewById(R.id.categorySelectorID);
		int pos = ((ArrayAdapter<String>)spinner.getAdapter()).getPosition(category);
	    spinner.setSelection(pos);
	}
	/*
	 * Methods for animation are below
	 */
	private void attachSwipeListener(View viewBin)
	{
		viewBin.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				return CheckOutActivity.this.gestureDetector.onTouchEvent(event);
			}
		});
	}

	public void swipeRight()
	{
		if (this.catKeys.size() == 1) return;

		final View view = this.catKeyToView.get(this.currentCategory);
		Animation animation = new TranslateAnimation(0, 1000,0, 0); //May need to check the direction you want.
		animation.setDuration(400);
		animation.setFillAfter(true);
		view.startAnimation(animation);
		final LinearLayout parentLayout = (LinearLayout) this.findViewById(R.id.checkOutScrollBin);
		
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				parentLayout.removeView(view);
				
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationStart(Animation arg0) {
			}
		});
		int index = this.catKeys.indexOf(this.currentCategory);
		--index;
		if (index < 0)
		{
			index = this.catKeys.size() - 1;
		}
		
		this.currentCategory = this.catKeys.get(index);
		final View nextView = this.catKeyToView.get(this.currentCategory);
		//parentLayout.removeAllViews();
		parentLayout.addView(nextView);
		animation = new TranslateAnimation(-500, 0,0, 0); //May need to check the direction you want.
		animation.setDuration(400);
		animation.setFillAfter(true);
		nextView.startAnimation(animation);
		this.setSpinner(this.currentCategory);
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				CheckOutActivity.this.gestureDetectorInner.tellIsFinished();
				CheckOutActivity.this.switchToViewByCategory(currentCategory);

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				
			}
		});
	}
	public void swipeLeft()
	{
		if (this.catKeys.size() == 1) return;
		
		final View view = this.catKeyToView.get(this.currentCategory);
		Animation animation = new TranslateAnimation(0, -1000,0, 0); //May need to check the direction you want.
		animation.setDuration(400);
		animation.setFillAfter(true);
		view.startAnimation(animation);
		final LinearLayout parentLayout = (LinearLayout) this.findViewById(R.id.checkOutScrollBin);

		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				parentLayout.removeView(view);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
			}
		});
		int index = this.catKeys.indexOf(this.currentCategory);
		++index;
		if (index >= this.catKeys.size())
		{
			index = 0;
		}
		
		this.currentCategory = this.catKeys.get(index);
		final View nextView = this.catKeyToView.get(this.currentCategory);
		//parentLayout.removeAllViews();
		parentLayout.addView(nextView);
		animation = new TranslateAnimation(1000, 0,0, 0); //May need to check the direction you want.
		animation.setDuration(400);
		animation.setFillAfter(true);
		nextView.startAnimation(animation);
		this.setSpinner(this.currentCategory);
		
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				CheckOutActivity.this.gestureDetectorInner.tellIsFinished();
				CheckOutActivity.this.switchToViewByCategory(currentCategory);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				
			}
		});
		//view.setVisibility(View.GONE);
	}

	class MyGestureDetector extends SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        private boolean isFinished = true;
        
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    //Toast.makeText(CheckOutActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
                	if (isFinished)
                	{
                		Log.i("TAG","Left swiping");
                		this.isFinished = false;
                		CheckOutActivity.this.swipeLeft();
                	}
                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	if (isFinished)
                	{
                		Log.i("TAG","Right swiping");
                		this.isFinished = false;
                		CheckOutActivity.this.swipeRight();
                	}                	//Toast.makeText(CheckOutActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
        public void tellIsFinished()
        {
        	this.isFinished = true;
        }
            @Override
        public boolean onDown(MotionEvent e) {
              return true;
        }
    }
	
}
