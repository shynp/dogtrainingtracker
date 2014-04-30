package com.upenn.trainingtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class TrainingReader 
{
	
	private static TrainingReader reader;
	private ArrayList<String> parentCategories;
	// Parent is like FOB and sub category such as Heel
	private HashMap<String, List<String>> parentToSub;
	private Map<String, String> subToParent = new HashMap<String, String>();
	/* The key is the string that is used when creating the table for this category
	 * We didn't use the category string itself since this would have spaces etc.
	 * and could also be subject to change
	 */
	// Such as heel, Walk it plank, etc.
	private Map<String, String> categoryToKey;
	private Map<String, String> keyToCategory;
	
	// Sub as heel, Walk it plank, etc
	private Map<String, String> categoryKeyToFileName;
	private Map<String, ArrayList<PlanEntry>> catKeyToCompositionList;
	private Map<String, Map<String,PlanEntry>> catKeyToCompositionMap;
	private boolean isInitialized;
	
	private Context activity;
	
	public enum CompositionType{
		List, Map
	}
	
	private TrainingReader(Context activity)
	{
		this.activity = activity;
		this.parentCategories = new ArrayList<String>();
		this.parentToSub = new HashMap<String, List<String>>();
		this.categoryToKey = new HashMap<String, String>();
		this.categoryKeyToFileName = new HashMap<String,String>();
		this.keyToCategory = new HashMap<String, String>();
		
		this.catKeyToCompositionList = new HashMap<String, ArrayList<PlanEntry>>();
		this.catKeyToCompositionMap = new HashMap<String, Map<String, PlanEntry>>();
	}
	public String getParentCatFromChild(String catKey)
	{
		if (this.subToParent.get(catKey) == null)
		{
			Log.i("TAG","RETURNING NULL");
		}
		return this.subToParent.get(catKey);
	}
	public String categoryToCatKey(String category)
	{
		Log.i("TAG","Getting cateogry key for: " + category);
		return this.categoryToKey.get(category);
	}
	public String catKeyToCategory(String catKey)
	{
		return this.keyToCategory.get(catKey);
	}
	public boolean isInitialized()
	{
		return this.isInitialized;
	}
	public static TrainingReader getInstance(Context activity)
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
	public List<PlanEntry> getViewCompositionListByCategoryKey(String categoryKey)
	{
		this.initializeCateogryInfo(categoryKey);
		return this.catKeyToCompositionList.get(categoryKey);
	}
	public Map<String, PlanEntry> getViewCompositionMapByCategoryKey(String categoryKey)
	{
		this.initializeCateogryInfo(categoryKey);
		return this.catKeyToCompositionMap.get(categoryKey);
	}
	/*
	 * Initialize info for a particular caregory (ex. Heel). Initializes both catKeyToCompositionList and
	 * catKeyToCompositionMap
	 */
	public void initializeCateogryInfo(String categoryKey)
	{
		// Check if already initialized
		if (this.catKeyToCompositionList.containsKey(categoryKey))
		{
			return;
		}
		
		if(!this.categoryKeyToFileName.containsKey(categoryKey)) throw new IllegalArgumentException("Categor does not exist: " + categoryKey);
		String fileName = this.categoryKeyToFileName.get(categoryKey);
		
		// INITIALIZATION
		Log.i("TAG","For file: " + fileName);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(activity.getAssets().open(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(in);
		
		// LINE SKIPPING
		// Skip the first line that says "Session"
		sc.nextLine(); 
		int numSessionLines = Integer.parseInt(sc.nextLine());
		// Session info is always the same so skip these
		for (int index = 0; index < numSessionLines; ++index) sc.nextLine(); 
		sc.nextLine(); // Skip the line that says "Trials"

		// INITIALIZE DATA STRUCTURES
		ArrayList<PlanEntry> entries = new ArrayList<PlanEntry>();
		Map<String, PlanEntry> entryMap = new HashMap<String, PlanEntry>();

		int numTrials = Integer.parseInt(sc.nextLine());

		while (sc.hasNext())
		{
			String line = sc.nextLine();
			String[] parts = line.split(",");
			
			// FIRST TOKEN
			String nameToken = parts[0]; // subcategory name such as 'distance'
			String[] nameParts = nameToken.split(Pattern.quote("||"));
			if (nameParts.length != 2) throw new IllegalArgumentException("Name token needs 2 parts: " + line);
			
			String name = nameParts[0];
			String nameKey = nameParts[1];
			
			if (parts[1].length() != 1) throw new IllegalArgumentException("Invalid type detected: " + parts[1]);

			// SECOND TOKEN
			char type = parts[1].charAt(0);

			if (PlanEntry.typeFromCharacter(type) == PlanEntry.Type.CHECKBOX)
			{
				PlanEntry entry = new PlanEntry(name, nameKey, type);
				entries.add(entry);
				entryMap.put(nameKey, entry);
				continue;
			}
			int numOptions = Integer.parseInt(parts[2]);
			if (parts.length != numOptions + 3)
			{
				Log.i("TAG", "INVALID LINE: " + line);
				for (String str : parts)
				{
					Log.i("TAG",str);
				}
				throw new IllegalArgumentException("Invalid number of options, expected: " + numOptions + " actual: " + (parts.length - 3) +
						" on line: " + line);
			}
			Log.i("TAG",line);
			String[] options = new String[numOptions];
			String[] optionKeys = new String[numOptions];
			
			for (int optionsIndex = 0; optionsIndex < numOptions; ++optionsIndex)
			{
				String param = parts[optionsIndex + 3];
    			String[] paramParts = param.split(Pattern.quote("||"));
    			
    			if (paramParts.length != 2) throw new IllegalArgumentException("Param needs 2 parts: " + line);
				options[optionsIndex] = paramParts[0];
				optionKeys[optionsIndex] = paramParts[1];
			}
			Log.i("TAG","Adding entry named " + name + " of type " + type);

			PlanEntry entry = new PlanEntry(name, nameKey, type, options, optionKeys);
			entries.add(entry);
			entryMap.put(nameKey, entry);
		}
		
		this.catKeyToCompositionList.put(categoryKey, entries);
		this.catKeyToCompositionMap.put(categoryKey, entryMap);		
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
    			if (parts.length != 3) throw new IllegalArgumentException("All subcategories must have key and filename: " + subValue);
    			this.categoryKeyToFileName.put(parts[1].trim(), parts[2].trim());
    			subList.add(parts[0].trim());
        		this.categoryToKey.put(parts[0].trim(), parts[1]);
        		this.keyToCategory.put(parts[1].trim(), parts[0].trim());
        		this.subToParent.put(parts[1].trim(), category);
    		}
    		this.parentToSub.put(category, subList);
    	}
	}


}
