package com.upenn.trainingtracker.customviews;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.upenn.trainingtracker.ImageManager;
import com.upenn.trainingtracker.PlanEntry;
import com.upenn.trainingtracker.R;
import com.upenn.trainingtracker.TrainingReader;
import com.upenn.trainingtracker.ViewUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryEntryWidget extends LinearLayout implements Comparable<HistoryEntryWidget>
{
	private LinearLayout planLayout;
	private TableLayout table;
	private boolean planVisible;
	private LinearLayout resultsBin;
	private HistoryEntryWidget.Type type;
	private Calendar date;
	
	public enum Type 
	{
		Passed, Failed, Aborted, Planned
	}
	
	public HistoryEntryWidget(Context context)
	{
		super(context);
	}
	public HistoryEntryWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public HistoryEntryWidget(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	public void initializeView(String catKey, Map<String, String> planMap, Calendar date)
	{
		this.resultsBin = (LinearLayout) this.findViewById(R.id.resultsBin);
		
		this.initializePlanTable(catKey, planMap);
		this.setCollapseBehavior();
		this.date = date;
	}
	public HistoryEntryWidget.Type getType()
	{
		return this.type;
	}
	public void initializeSuccessFailureButtons(List<Boolean> resultSequence)
	{
		Log.i("TAG","RESULT LENGTH: " + resultSequence.size());
		int numSuccess = 0;
		for (Boolean sf : resultSequence)
		{
			if (sf) ++numSuccess;
			this.addSuccessFailureButton(sf);
		}
		if (numSuccess >= 4) // success
		{
			type = Type.Passed;
			this.setBackground(this.getResources().getDrawable(R.drawable.history_widget_passed));
		}
		else if (numSuccess < 4 && resultSequence.size() == 5) // failed
		{
			type = Type.Failed;
			this.setBackground(this.getResources().getDrawable(R.drawable.history_widget_failed));
		}
		else if (numSuccess < 4 && resultSequence.size() > 0) // aborted
		{
			type = Type.Aborted;
			this.setBackground(this.getResources().getDrawable(R.drawable.history_widget_aborted));
		}
		else if (resultSequence.size() == 0) // planned
		{
			type = Type.Planned;
			this.setBackground(this.getResources().getDrawable(R.drawable.history_widget_planned));
		}
		int padding = (int)ViewUtils.convertDpToPixel(10, this.getContext());
		this.setPadding(padding, padding, padding, padding);
	}
	private void addSuccessFailureButton(boolean sf)
	{
		Button button = null;
		final LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (sf) // success
		{
			button = (Button) inflater.inflate(R.layout.session_success_button, null);
		}
		else
		{
			button = (Button) inflater.inflate(R.layout.session_failure_button, null);
		}
		int dim = (int) ViewUtils.convertDpToPixel(20, getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dim, dim);
		params.setMargins(0, 0, (int) ViewUtils.convertDpToPixel(2, getContext()), 0);
		button.setLayoutParams(params);
		resultsBin.addView(button);
	}
	private void initializePlanTable(String catKey, Map<String, String> planMap)
	{
		TrainingReader reader = TrainingReader.getInstance(this.getContext());
		Map<String, PlanEntry> keyToEntry = reader.getViewCompositionMapByCategoryKey(catKey);
		
		Iterator<String> iter = planMap.keySet().iterator();
		LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.table = (TableLayout) this.findViewById(R.id.tableID);
		this.planLayout = (LinearLayout) this.findViewById(R.id.planLayout);
		
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
			else if (entry.getType() == PlanEntry.Type.IMAGE_OPTIONS)
			{
				optionText.setText("");
				ImageManager manager = ImageManager.getInstance();
				Integer drawableID = manager.keyToDrawableID(optionValue);
				Drawable optionImage = this.getContext().getResources().getDrawable(drawableID);
				optionText.setCompoundDrawablesWithIntrinsicBounds(optionImage, null, null, null);
			}
			else
			{
				optionText.setText(optionValue.equals("1") ? "True" : "False");
			}
			this.table.addView(row);
		}
		this.removeView(this.planLayout);
		this.invalidate();
	}
	private void setCollapseBehavior()
	{
		TextView text = (TextView) this.findViewById(R.id.collapseID);
		text.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view) 
			{
				if (HistoryEntryWidget.this.planVisible)
				{
					HistoryEntryWidget.this.collapseView();
				}
				else
				{
					HistoryEntryWidget.this.explandView();
				}
				Log.i("TAG", (HistoryEntryWidget.this.planVisible ? "Visible" : "Hidden"));
			}	
		});
		
	}
	public void collapseView()
	{
		if (!this.planVisible)
		{
			return;
		}
		this.removeView(planLayout);
		this.planVisible = false;
		this.invalidate();
	}
	public void explandView()
	{
		if (this.planVisible)
		{
			return;
		}
		this.addView(planLayout);
		this.planVisible = true;
		this.invalidate();
	}
	@Override
	public int compareTo(HistoryEntryWidget otherWidget) 
	{
		return this.date.compareTo(otherWidget.date) * -1;
	}
}
