package com.upenn.trainingtracker.customviews;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
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
	public String getCheckBoxValueByCategory(String categoryKey)
	{
		CheckBox check = this.checkBoxMap.get(categoryKey);
		return (check.isChecked() ? "1" : "0");
	}
	

}