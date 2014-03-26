package com.upenn.trainingtracker.customviews;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class DateSelectorTextView extends EditText
{
	private Calendar dateOfBirth;	
	private FragmentActivity parentFragment;
	
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
	{
		private DateSelectorTextView dateTextView;
		
		public void setParent(DateSelectorTextView dateTextView)
		{
			this.dateTextView = dateTextView;
		}
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) 
	    {
	        final Calendar c = Calendar.getInstance();
	        int year = -1;
	        int month = -1;
	        int day = -1;
	        
	    	if (this.dateTextView.dateOfBirth != null)
	    	{
		        year = dateTextView.dateOfBirth.get(Calendar.YEAR);
		        month = dateTextView.dateOfBirth.get(Calendar.MONTH);
		        day = dateTextView.dateOfBirth.get(Calendar.DAY_OF_MONTH);
	    	}
	    	else
	    	{
		        // Use the current date as the default date in the picker
		        year = c.get(Calendar.YEAR);
		        month = c.get(Calendar.MONTH);
		        day = c.get(Calendar.DAY_OF_MONTH);
	    	}
	        // Create a new instance of DatePickerDialog and return it
	        return new DatePickerDialog(getActivity(), this, year, month, day);
	    }

		@Override
		public void onDateSet(DatePicker view, int year, int month,
				int day) 
		{
			dateTextView.setDate(year, month, day);
		}
		
	}
	
	public DateSelectorTextView(Context context) {
		super(context);
		this.init();
	}
	public void setDate(int year, int month, int day)
	{
		if (this.dateOfBirth == null)
		{
			this.dateOfBirth = GregorianCalendar.getInstance();
		}
		this.dateOfBirth.set(year,  month, day);
		this.setDateText();
	}
	public void setDate(Calendar cal)
	{
		this.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}
    public Calendar getDateOfBirth()
    {
    	return this.dateOfBirth;
    }
	public void setDateText() 
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String dateString = sdf.format(this.dateOfBirth.getTime());
		String text = "DOB: " + dateString;
		this.setText(text);
	}
	public DateSelectorTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}
	public DateSelectorTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}
	public void setParentFragment(FragmentActivity parentFragment)
	{
		this.parentFragment = parentFragment;
	}
	public void init()
	{
		this.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					DatePickerFragment frag = new DatePickerFragment();
					frag.setParent(DateSelectorTextView.this);
					frag.show(DateSelectorTextView.this.parentFragment.getSupportFragmentManager(), "datePicker");
				}
				return true;	
			}
		});
	}
	
	

}
