package com.upenn.trainingtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import android.app.Activity;
import android.util.Log;

public class TrainingReader 
{
	private static TrainingReader reader;
	private ArrayList<String> parentCategories;
	private HashMap<String, List<String>> parentToSub;
	private HashMap<String, String> subToFileName;
	private boolean isInitialized;
	
	private Activity activity;
	
	private TrainingReader(Activity activity)
	{
		this.activity = activity;
		this.parentCategories = new ArrayList<String>();
		this.parentToSub = new HashMap<String, List<String>>();
		this.subToFileName = new HashMap<String, String>();
	}
	public static TrainingReader getInstance(Activity activity)
	{
		if (reader == null) reader = new TrainingReader(activity);
		return TrainingReader.reader;
	}
	public ArrayList<String> getParentCategories()
	{
		Log.i("TAG", "here");
		if (!this.isInitialized)
		{
			this.initializeParentAndSubCategories();
			this.isInitialized = true;
		}
		return this.parentCategories;
	}
	public List<String> getAllCategories()
	{
		if (!this.isInitialized)
		{
			this.initializeParentAndSubCategories();
			this.isInitialized = true;
		} 
		List<String> all = new ArrayList<String>();
		for (String parent : this.parentCategories)
		{
			all.addAll(this.parentToSub.get(parent));
		}
		return all;
	}
	public List<String> getSubCategories(String parentCategory)
	{
		if (!this.isInitialized)
		{
			this.initializeParentAndSubCategories();
			this.isInitialized = true;
		}
		return this.parentToSub.get(parentCategory);
	}
	private void initializeParentAndSubCategories()
	{
		Log.i("TAG", "Initialzigin");
		BufferedReader in = null;
    	try {
			in = new BufferedReader(new InputStreamReader(activity.getAssets().open("index_file.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	Scanner sc = new Scanner(in);
    	sc.useDelimiter(",");
    	Log.i("TAG","Reading using delimiter");
    	while (sc.hasNext())
    	{
    		String category = sc.next().trim();
    		this.parentCategories.add(category);
    		Log.i("TAG","Reading for category: " + category);
    		int numSub = Integer.parseInt(sc.next().trim());
			List<String> subList = new ArrayList<String>();

    		for (int index = 0; index < numSub; ++index)
    		{
    			String subValue = sc.next();
    			String[] parts = subValue.split(Pattern.quote("||"));
    			if (parts.length != 2) throw new IllegalArgumentException("All subcategories must have filename on: " + parts.length);
    			this.subToFileName.put(parts[0].trim(), parts[1].trim());
    			subList.add(parts[0].trim());
    		}
    		this.parentToSub.put(category, subList);
    	}
	}


}
