package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.upenn.trainingtracker.customviews.AutoCategorySelector;
import com.upenn.trainingtracker.customviews.PlanningBinLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
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
	Map<String, ArrayList<PlanEntry>> categoryToPlanEntries;
	Map<String, PlanningBinLayout> categoryToView;
	
	Map<PlanEntry, View> planEntryToView;
	
	
	private String currentCategory;
	private List<String> categories;
	private GestureDetector gestureDetector;
	private CheckOutActivity.MyGestureDetector gestureDetectorInner;
	private int dogID;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.check_out_layout);
		View parentView = this.findViewById(R.id.checkOutParentView);
		this.attachSwipeListener(parentView);
		Bundle extras = this.getIntent().getExtras();
		
		String[] categories = extras.getStringArray("categories");
		this.categories = Arrays.asList(categories);
		
		this.planEntryToView = new HashMap<PlanEntry, View>();
		this.dogID = extras.getInt("dogID");
		this.currentCategory = categories[0];
		gestureDetectorInner = new MyGestureDetector();
		gestureDetector = new GestureDetector(this, gestureDetectorInner);
		this.initializeLayout(categories);
	}
	private void initializeLayout(String[] categories)
	{
		Spinner spinner = (Spinner) this.findViewById(R.id.categorySelectorID);
		
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int position, long arg3) {
				String category = parent.getItemAtPosition(position).toString();
				CheckOutActivity.this.switchToViewByCategory(category);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		TrainingReader reader = TrainingReader.getInstance(this);
		this.categoryToPlanEntries = new HashMap<String, ArrayList<PlanEntry>>();
		this.categoryToView = new HashMap<String, PlanningBinLayout>();
				
		// Initialize category to entries structure and category to view structure
		for (String category : categories)
		{
			ArrayList<PlanEntry> entries = reader.getViewCompositionByCategory(category);
			this.categoryToPlanEntries.put(category, entries);
			this.categoryToView.put(category, this.getViewForCategory(category));
		}
		// Set view to current view
		this.switchToViewByCategory(this.currentCategory);
	}
	public void recordPlanInformation()
	{
		DatabaseHandler db = new DatabaseHandler(this);
		for (String category : this.categories)
		{
			PlanningBinLayout binLayout = this.categoryToView.get(category);
			ArrayList<PlanEntry> entries = this.categoryToPlanEntries.get(category);
			
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
			db.addPlan(plan, category, dogID);
			Log.i("TAG","Plan for " + category + ": " + plan);
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
		ArrayList<PlanEntry> entries = this.categoryToPlanEntries.get(this.currentCategory);
		
		for (PlanEntry entry : entries)
		{
			for (String category : this.categories)
			{
				if (category.equals(this.currentCategory)) continue;
				
				ArrayList<PlanEntry> otherEntries = this.categoryToPlanEntries.get(category);
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
	private void switchToViewByCategory(String category)
	{
		LinearLayout parentLayout = (LinearLayout) this.findViewById(R.id.checkOutScrollBin);
		parentLayout.removeAllViews();
		View view = this.categoryToView.get(category);
		this.currentCategory = category;
		parentLayout.addView(view);
	}
	private PlanningBinLayout getViewForCategory(String category)
	{
		DatabaseHandler handler = new DatabaseHandler(this);
    	
		Map<String, String> plan = handler.getPlan(category, this.dogID);
		
		Log.i("TAG","------------Getting view for category: " + category);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		PlanningBinLayout viewBinParent = (PlanningBinLayout) inflater.inflate(R.layout.check_out_view_bin, null);
		TableLayout viewBin = (TableLayout) viewBinParent.findViewById(R.id.tableLayoutBin);

		for (PlanEntry entry: this.categoryToPlanEntries.get(category))
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
	private Spinner getImageSpinnerFromEntry(PlanEntry entry)
	{
		return null; //TODO: IMPLEMENT THIS
	}

	private void setSpinner(String category)
	{
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
		if (this.categories.size() == 1) return;

		final View view = this.categoryToView.get(this.currentCategory);
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
		int index = this.categories.indexOf(this.currentCategory);
		--index;
		if (index < 0)
		{
			index = this.categories.size() - 1;
		}
		
		this.currentCategory = this.categories.get(index);
		final View nextView = this.categoryToView.get(this.currentCategory);
		//parentLayout.removeAllViews();
		parentLayout.addView(nextView);
		animation = new TranslateAnimation(-1000, 0,0, 0); //May need to check the direction you want.
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
		if (this.categories.size() == 1) return;
		
		final View view = this.categoryToView.get(this.currentCategory);
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
		int index = this.categories.indexOf(this.currentCategory);
		++index;
		if (index >= this.categories.size())
		{
			index = 0;
		}
		
		this.currentCategory = this.categories.get(index);
		final View nextView = this.categoryToView.get(this.currentCategory);
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
