package com.upenn.trainingtracker.customviews;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.upenn.trainingtracker.PlanEntry;
import com.upenn.trainingtracker.R;
import com.upenn.trainingtracker.TrainingReader;
import com.upenn.trainingtracker.ViewUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SessionCategoryWidget extends LinearLayout
{
	private boolean tableVisible = false;
	private TableLayout table;
	private LinearLayout resultsBin;
	
	private List<View> resultViews = new ArrayList<View>();
	
	public SessionCategoryWidget(Context context)
	{
		super(context);
	}
	public SessionCategoryWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SessionCategoryWidget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	public void initializeView(String catKey, Map<String, String> planMap)
	{
		this.setCollapseBehavior();
		this.initializePlanTable(catKey, planMap);
		this.setSuccessFailureButtons();
		this.setDeleteLastButton();
		TrainingReader reader = TrainingReader.getInstance(this.getContext());
		String catName = reader.catKeyToCategory(catKey);
		TextView titleText = (TextView) this.findViewById(R.id.title);
		titleText.setText(catName);
	}
	private void setDeleteLastButton()
	{
		Button deleteLastButton = (Button) this.findViewById(R.id.delete);
		deleteLastButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
		    	AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		    	builder.setMessage("Would you like to delete the last trial?");
		    	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
		    	{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						if (resultViews.isEmpty()) return;
						int index = resultViews.size() - 1;
						SessionCategoryWidget.this.resultsBin.removeView(resultViews.get(index));
						resultViews.remove(resultViews.size() - 1);
						SessionCategoryWidget.this.invalidate();
					}
		    	});
		    	builder.setNegativeButton("No", null);
		    	builder.create().show();				
			}
		});
	}
	private void setSuccessFailureButtons()
	{
		Button successButton = (Button) this.findViewById(R.id.success);
		Button failureButton = (Button) this.findViewById(R.id.failure);
		
		
		resultsBin = (LinearLayout) this.findViewById(R.id.resultsBin);
		final LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		successButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				Button button = (Button) inflater.inflate(R.layout.session_success_button, null);
				int dim = (int) ViewUtils.convertDpToPixel(20, getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dim, dim);
				params.setMargins(0, 0, (int) ViewUtils.convertDpToPixel(2, getContext()), 0);
				button.setLayoutParams(params);
				resultsBin.addView(button);
				resultViews.add(button);
			}
		});
		failureButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				Button button = (Button) inflater.inflate(R.layout.session_failure_button, null);
				int dim = (int) ViewUtils.convertDpToPixel(20, getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dim, dim);
				params.setMargins(0, 0, (int) ViewUtils.convertDpToPixel(2, getContext()), 0);
				button.setLayoutParams(params);
				resultsBin.addView(button);
				resultViews.add(button);
			}
		});
	}
	private void initializePlanTable(String catKey, Map<String, String> planMap)
	{
		TrainingReader reader = TrainingReader.getInstance(this.getContext());
		Map<String, PlanEntry> keyToEntry = reader.getViewCompositionMapByCategoryKey(catKey);
		
		Iterator<String> iter = planMap.keySet().iterator();
		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.table = (TableLayout) this.findViewById(R.id.tableID);
		
		while (iter.hasNext())
		{
			String key = iter.next();
			String optionValue = planMap.get(key);
			TableRow row = (TableRow) inflater.inflate(com.upenn.trainingtracker.R.layout.session_table_row, null);
			TextView keyText = (TextView) row.findViewById(R.id.key);
			TextView optionText = (TextView) row.findViewById(R.id.value);
			PlanEntry entry = keyToEntry.get(key);
			
			keyText.setText(entry.getName());

			if (entry.getType() == PlanEntry.Type.OPTIONS)
			{
				optionText.setText(entry.getOptionFromOptionKey(optionValue));
			}
			else
			{
				optionText.setText(optionValue.equals("1") ? "True" : "False");
			}
			this.table.addView(row);
		}
		SessionCategoryWidget.this.removeView(this.table);
		SessionCategoryWidget.this.invalidate();
	}
	private void setCollapseBehavior()
	{
		this.table = (TableLayout) this.findViewById(R.id.tableID);
		TextView text = (TextView) this.findViewById(R.id.collapseID);
		text.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				if (SessionCategoryWidget.this.tableVisible)
				{
					//SessionCategoryWidget.this.table.setVisibility(View.INVISIBLE);
					SessionCategoryWidget.this.removeView(table);
				}
				else
				{
					SessionCategoryWidget.this.addView(table);
					//SessionCategoryWidget.this.table.setVisibility(View.VISIBLE);
				}
				SessionCategoryWidget.this.tableVisible = !SessionCategoryWidget.this.tableVisible;
				SessionCategoryWidget.this.invalidate();
				Log.i("TAG", (SessionCategoryWidget.this.tableVisible ? "Visible" : "Hidden"));
			}	
		});
		
	}
	
}
