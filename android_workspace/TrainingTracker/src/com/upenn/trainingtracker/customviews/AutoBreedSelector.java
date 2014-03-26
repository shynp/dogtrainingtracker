package com.upenn.trainingtracker.customviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class AutoBreedSelector extends AutoCompleteTextView
{
	private Activity parent;
	private Vector<String> strings;
	
	public AutoBreedSelector(Context context)
	{
		super(context);
	}
	public AutoBreedSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public AutoBreedSelector(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	public void initializeAutoBreeder(Activity parent)
	{
		this.parent = parent;
		this.setAutoText();
		this.setFocusBehavior();
	}
	private void setFocusBehavior()
	{
		this.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus) 
			{	
				if (!hasFocus)
				{
					String input = AutoBreedSelector.this.getText().toString();
					if (input.trim().equals("")) return;
					if (!AutoBreedSelector.this.strings.contains(input))
					{
						AutoBreedSelector.this.setText("");
						Toast.makeText(AutoBreedSelector.this.parent, "Invalid Breed", Toast.LENGTH_LONG).show();
					}
				}
			}	
		});
	}
	public boolean setValue(String breed)
	{
		if (this.strings.contains(breed))
		{
			this.setText(breed);
			return true;
		}
		return false;
	}
	private void setAutoText()
	{
    	strings = new Vector<String>();
    	
    	BufferedReader in = null;
    	try {
			in = new BufferedReader(new InputStreamReader(parent.getAssets().open("breeds.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String line = null;
		try {
			line = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	while (line != null)
    	{
    		strings.add(line);
    		try {
    			line = in.readLine();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent,
                android.R.layout.simple_dropdown_item_1line, strings);
    	this.setAdapter(adapter);
	}
}
