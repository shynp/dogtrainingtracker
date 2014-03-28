package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.List;

import com.upenn.trainingtracker.customviews.AutoCategorySelector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class TrainingSelectorActivity extends Activity
{
	private List<String> selectedCategories;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.training_selector_layout);
		this.selectedCategories = new ArrayList<String>();
		AutoCategorySelector categorySelector = (AutoCategorySelector) this.findViewById(R.id.categorySelectorID);
		categorySelector.setParentAndInitialize(this);
		
		this.addLookUpButtons();
	}
	public void addNewCategory(final String category)
	{
		if (this.selectedCategories.contains(category)) return;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		this.selectedCategories.add(category);
		final LinearLayout selectedLayout = (LinearLayout) this.findViewById(R.id.selectedLayout);
		final Button button = (Button) inflater.inflate(R.layout.lookup_selected_widget, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.bottomMargin = 10;
		params.rightMargin = 5;
		params.leftMargin = 5;
		params.topMargin = 10;
		button.setLayoutParams(params);
		
		button.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) 
			{
				String[] items = new String[] {"Remove Category", "Cancel"};
				ArrayAdapter<String> adapter = new ArrayAdapter<String> (TrainingSelectorActivity.this, android.R.layout.select_dialog_item, items);
				AlertDialog.Builder builder = new AlertDialog.Builder(TrainingSelectorActivity.this);
				builder.setTitle("Dog Selection");
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog, int item) 
					{
						if (item == 0) // Remove Category
						{
							TrainingSelectorActivity.this.selectedCategories.remove(category);
							selectedLayout.removeView(button);
						}
						else if (item == 1) // Cancel
						{
							
						}

					}
				});
				builder.create().show();
			}
		});
		button.setText(category);
		selectedLayout.addView(button);
	}
	public void addLookUpButtons()
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		

		TrainingReader reader = TrainingReader.getInstance(this);
		List<String> categories = reader.getParentCategories();
		int rowCount = categories.size() / 2 + categories.size() % 2;
		
		LinearLayout parent = (LinearLayout) this.findViewById(R.id.lookupLayout);
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int buttonWidth = (size.x - 80)/2;
		for (int index = 0; index < categories.size(); ++index)
		{
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.HORIZONTAL);
			
			Button button = (Button) inflater.inflate(R.layout.lookup_widget, null);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			params.bottomMargin = 10;
			params.rightMargin = 10;
			params.leftMargin = 10;
			params.topMargin = 10;
			
			button.setLayoutParams(params);
			
			button.setWidth(buttonWidth);
			button.setText(categories.get(index));
			this.configureListenerWithSubcategories(button, categories.get(index));
			layout.addView(button);
			++index;
			if (index == categories.size())
			{
				parent.addView(layout);
				return;
			}
			Button button2 = (Button) inflater.inflate(R.layout.lookup_widget, null);
			button2.setLayoutParams(params);
			button2.setWidth(buttonWidth);
			button2.setText(categories.get(index));
			this.configureListenerWithSubcategories(button2, categories.get(index));

			layout.addView(button2);
			Log.i("TAG","Adding view");
			parent.addView(layout);
		}
	}
	public void configureListenerWithSubcategories(Button button, String parentCategory)
	{
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TrainingReader reader = TrainingReader.getInstance(this);
		final List<String> categories = reader.getSubCategories(parentCategory);
		Log.i("TAG","num: " + categories.size());
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				final Dialog dialog = new Dialog(TrainingSelectorActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.category_dialog_layout);
				
				LinearLayout parent = (LinearLayout) dialog.findViewById(R.id.categoryLayout);
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int buttonWidth = (size.x - 100)/2;
				Log.i("TAG","width: " + buttonWidth);
				for (int index = 0; index < categories.size(); ++index)
				{
					LinearLayout layout = new LinearLayout(TrainingSelectorActivity.this);
					layout.setOrientation(LinearLayout.HORIZONTAL);
					
					Button button = (Button) inflater.inflate(R.layout.lookup_widget, null);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
					params.bottomMargin = 10;
					params.rightMargin = 10;
					params.leftMargin = 10;
					params.topMargin = 10;
					
					button.setLayoutParams(params);
					
					button.setWidth(buttonWidth);
					button.setText(categories.get(index));
					final int indexFinal = index;
					button.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v) {
							TrainingSelectorActivity.this.addNewCategory(categories.get(indexFinal));
							dialog.cancel();
						}
					});
					layout.addView(button);
					++index;
					if (index == categories.size())
					{
						parent.addView(layout);
						dialog.show();
						return;
					}

					
					Button button2 = (Button) inflater.inflate(R.layout.lookup_widget, null);
					button2.setLayoutParams(params);
					button2.setWidth(buttonWidth);
					button2.setText(categories.get(index));
					final int indexFinal2 = index;
					button2.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v) {
							TrainingSelectorActivity.this.addNewCategory(categories.get(indexFinal2));
							dialog.cancel();
						}
					});
					layout.addView(button2);
					Log.i("TAG","Adding view");
					parent.addView(layout);
				}
				Log.i("TAG","showing dialog");
				dialog.show();
			}
			
		});
	}
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 * 
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @param context Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context){
	    Resources resources = context.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float dp = px / (metrics.densityDpi / 160f);
	    return dp;
	}
}
