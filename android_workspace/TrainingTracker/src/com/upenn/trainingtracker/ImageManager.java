package com.upenn.trainingtracker;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

public class ImageManager 
{
	private static ImageManager instance;
	private Map<String, Integer> keyToResource = new HashMap<String, Integer>();
	
	private ImageManager()
	{
		keyToResource.put("second", R.drawable.second);
		keyToResource.put("first_and_third", R.drawable.first_and_third);
		keyToResource.put("second_and_third", R.drawable.second_and_third);
		keyToResource.put("first_and_second", R.drawable.first_and_second);
		keyToResource.put("first_second_and_third", R.drawable.first_second_and_third);

	}
	public static ImageManager getInstance()
	{
		if (instance == null)
		{
			ImageManager.instance = new ImageManager();
		}
		return instance;
	}
	public int keyToDrawableID(String key)
	{
		return this.keyToResource.get(key);		
	}

}
