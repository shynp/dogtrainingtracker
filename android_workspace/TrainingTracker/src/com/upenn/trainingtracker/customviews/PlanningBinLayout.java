package com.upenn.trainingtracker.customviews;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;

public class PlanningBinLayout extends ScrollView
{
	private Map<String, CheckBox> checkBoxMap;
	private Map<String, Spinner> spinnerMap;
	private Map<String, String[]> catKeyToOptionsKeys;
	
	public PlanningBinLayout(Context context)
	{
		super(context);
		this.init();
	}
	public PlanningBinLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}
	private void init()
	{
		this.checkBoxMap = new HashMap<String, CheckBox>();
		this.spinnerMap = new HashMap<String, Spinner>();
		this.catKeyToOptionsKeys = new HashMap<String, String[]>();
	}
	
	public void registerCheckBox(String categoryKey, CheckBox check)
	{
		this.checkBoxMap.put(categoryKey, check);
	}
	public void registerSpinner(String categoryKey, String[] optionKeys, Spinner spinner)
	{
		this.spinnerMap.put(categoryKey, spinner);
		this.catKeyToOptionsKeys.put(categoryKey, optionKeys);
	}
	public String getSpinnerValueByCategory(String categoryKey)
	{
		Spinner spinner = this.spinnerMap.get(categoryKey);
		int index = spinner.getSelectedItemPosition();
		String[] optionKeys = this.catKeyToOptionsKeys.get(categoryKey);
		return optionKeys[index];
	}
	public void setSpinnerByKeys(String categoryKey, String optionsKey)
	{
		Spinner spinner = this.spinnerMap.get(categoryKey);
		if (spinner == null)
		{
			throw new IllegalArgumentException("Invalid categoryKey: " + categoryKey);
		}
		String[] options = this.catKeyToOptionsKeys.get(categoryKey);
		int valueIndex = -1;
		for (int index = 0; index < options.length; ++index)
		{
			if (options[index].equals(optionsKey))
			{
				valueIndex = index;
				break;
			}
		}
		if (valueIndex == -1) 
		{
			Log.e("TAG","Invalid optionsKey: " + optionsKey + " for " + categoryKey);
			//throw new IllegalArgumentException("Invalid optionsKey: " + optionsKey + " for " + categoryKey);
		}
		else
		{
			spinner.setSelection(valueIndex);
		}
	}
	public void setCheckBoxByKeys(String categoryKey, String boolValue)
	{
		Log.i("TAG",boolValue);
		CheckBox checkBox = this.checkBoxMap.get(categoryKey);
		if (checkBox == null)
		{
			throw new IllegalArgumentException("Invalid categoryKey: " + categoryKey);
		}
		if (boolValue.equals("1"))
		{
			checkBox.setChecked(true);
		}
		else
		{
			checkBox.setChecked(false);
		}
	}
	public String getCheckBoxValueByCategory(String categoryKey)
	{
		CheckBox check = this.checkBoxMap.get(categoryKey);
		return (check.isChecked() ? "1" : "0");
	}
	

}