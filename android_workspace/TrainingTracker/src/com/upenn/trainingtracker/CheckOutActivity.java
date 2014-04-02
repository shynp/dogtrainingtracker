package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.upenn.trainingtracker.customviews.AutoCategorySelector;

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
	Map<String, View> categoryToView;
	private String currentCategory;
	private List<String> categories;
	private GestureDetector gestureDetector;
	private CheckOutActivity.MyGestureDetector gestureDetectorInner;
	
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
				// TODO Auto-generated method stub
				String category = parent.getItemAtPosition(position).toString();
				CheckOutActivity.this.switchToViewByCategory(category);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		TrainingReader reader = TrainingReader.getInstance(this);
		this.categoryToPlanEntries = new HashMap<String, ArrayList<PlanEntry>>();
		this.categoryToView = new HashMap<String, View>();
				
		// Initialize category to entries structure and category to view structure
		for (String category : categories)
		{
			ArrayList<PlanEntry> entries = reader.getViewCompositionByCategory(category);
			this.categoryToPlanEntries.put(category, entries);
			this.categoryToView.put(category, this.getViewForCategory(category));
		}
		// Set view to current view
		//ScrollView parentLayout = (ScrollView) this.findViewById(R.id.checkOutScrollBin);
		//parentLayout.addView(this.categoryToView.get(this.currentCategory));
		this.switchToViewByCategory(this.currentCategory);
	}
	
	private void switchToViewByCategory(String category)
	{
		LinearLayout parentLayout = (LinearLayout) this.findViewById(R.id.checkOutScrollBin);
		parentLayout.removeAllViews();
		View view = this.categoryToView.get(category);
		this.currentCategory = category;
		parentLayout.addView(view);
	}
	
	
	
	/*
	 * Methods for rendering the views are below
	 */
	private View getViewForCategory(String category)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ScrollView viewBinParent = (ScrollView) inflater.inflate(R.layout.check_out_view_bin, null);
		TableLayout viewBin = (TableLayout) viewBinParent.findViewById(R.id.tableLayoutBin);
		for (PlanEntry entry: this.categoryToPlanEntries.get(category))
		{
			switch (entry.getType())
			{
			case CHECKBOX: 
				TableRow canvasLayout = (TableRow) this.getCheckBoxFromEntry(entry);
				viewBin.addView(canvasLayout);
			break;
			case OPTIONS: 
				TableRow canvasLayout2 = (TableRow) this.getSpinnerFromEntry(entry);
				viewBin.addView(canvasLayout2);
			break;
			case IMAGE_OPTIONS:// view = this.getImageSpinnerFromEntry(entry);
			break;
			}
		}
		this.attachSwipeListener(viewBin);
		return viewBinParent;
	}
	private void attachSwipeListener(View viewBin)
	{
		viewBin.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				return CheckOutActivity.this.gestureDetector.onTouchEvent(event);
			}
		});
	}
	private LinearLayout getCheckBoxFromEntry(PlanEntry entry)
	{
		Log.i("TAG","Adding checkbox");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout checkLayout = (LinearLayout) inflater.inflate(R.layout.options_check_widget, null);
		
		CheckBox check = (CheckBox) checkLayout.findViewById(R.id.optionsCheckWidget);
		//check.setText(entry.getName());
		
		TextView text = (TextView) checkLayout.findViewById(R.id.optionsCheckWidgetText);
		text.setText(entry.getName());
		
		
		return checkLayout;
	}
	private LinearLayout getSpinnerFromEntry(PlanEntry entry)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.options_widget, null);
		
		TextView text = (TextView) layout.findViewById(R.id.optionsTextID);
		text.setText(entry.getName());
		
		Spinner spinner = (Spinner) layout.findViewById(R.id.optionsSpinnerID);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, entry.getOptions());
		spinner.setAdapter(spinnerArrayAdapter);
		
		return layout;
	}
	private Spinner getImageSpinnerFromEntry(PlanEntry entry)
	{
		return null; //TODO: IMPLEMENT THIS
	}
	public void swipeRight()
	{
		final View view = this.categoryToView.get(this.currentCategory);
		Animation animation = new TranslateAnimation(0, 1000,0, 0); //May need to check the direction you want.
		animation.setDuration(400);
		animation.setFillAfter(true);
		view.startAnimation(animation);
		final LinearLayout parentLayout = (LinearLayout) this.findViewById(R.id.checkOutScrollBin);
		
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				parentLayout.removeView(view);
				
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				CheckOutActivity.this.gestureDetectorInner.tellIsFinished();
				CheckOutActivity.this.switchToViewByCategory(currentCategory);

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		//view.setVisibility(View.GONE);
	}
	public void swipeLeft()
	{
		final View view = this.categoryToView.get(this.currentCategory);
		Animation animation = new TranslateAnimation(0, -1000,0, 0); //May need to check the direction you want.
		animation.setDuration(400);
		animation.setFillAfter(true);
		view.startAnimation(animation);
		final LinearLayout parentLayout = (LinearLayout) this.findViewById(R.id.checkOutScrollBin);

		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				parentLayout.removeView(view);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				CheckOutActivity.this.gestureDetectorInner.tellIsFinished();
				CheckOutActivity.this.switchToViewByCategory(currentCategory);
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		//view.setVisibility(View.GONE);
	}
	private void setSpinner(String category)
	{
		Spinner spinner = (Spinner) this.findViewById(R.id.categorySelectorID);
		int pos = ((ArrayAdapter<String>)spinner.getAdapter()).getPosition(category);
	    spinner.setSelection(pos);
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
