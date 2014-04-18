package com.upenn.trainingtracker.test;

import java.util.List;

import junit.framework.Assert;

import com.upenn.trainingtracker.TrainingReader;

import android.test.AndroidTestCase;

public class TrainingReaderGetSubCategories extends AndroidTestCase 
{
	private TrainingReader reader;
	private List<String> categories;
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		reader = TrainingReader.getInstance(this.getContext());
	}
	@Override
	protected void tearDown() throws Exception 
	{
	 super.tearDown();
	}
	public void testTrivial() throws Throwable
	{
		Assert.assertEquals(1, 1);
	}
	public void testGetDAC() throws Throwable
	{
		categories = reader.getSubCategories("Direction and Control");
		AndroidTestCase.assertTrue(categories.contains("Go Out"));
		AndroidTestCase.assertTrue(categories.contains("Patterns"));
		AndroidTestCase.assertTrue(categories.contains("Hup and Off"));
		AndroidTestCase.assertTrue(categories.contains("Over"));
		AndroidTestCase.assertTrue(categories.contains("Back"));
		AndroidTestCase.assertTrue(categories.size() == 5);	
	}
	public void testGetAG() throws Throwable
	{
		categories = reader.getSubCategories("Agility");
		AndroidTestCase.assertTrue(categories.contains("Walk It Plank"));
		AndroidTestCase.assertTrue(categories.contains("Tunnel"));
		AndroidTestCase.assertTrue(categories.contains("Walk It Surface"));
		AndroidTestCase.assertTrue(categories.contains("Climb"));
		AndroidTestCase.assertTrue(categories.contains("Crawl"));
		AndroidTestCase.assertTrue(categories.contains("Balance"));
		AndroidTestCase.assertTrue(categories.size() == 6);	
	}
	public void testGetFOB() throws Throwable
	{
		categories = reader.getSubCategories("Foundation Obedience");
		AndroidTestCase.assertTrue(categories.contains("Stay"));
		AndroidTestCase.assertTrue(categories.contains("Sit / Stay"));
		AndroidTestCase.assertTrue(categories.contains("Come - Recall"));
		AndroidTestCase.assertTrue(categories.contains("Down"));
		AndroidTestCase.assertTrue(categories.contains("Heel"));
		AndroidTestCase.assertTrue(categories.contains("Down / Stay"));
		AndroidTestCase.assertTrue(categories.size() == 6);	
	}
	public void testGetSearch() throws Throwable 
	{
		categories = reader.getSubCategories("Search");
		AndroidTestCase.assertTrue(categories.contains("Search"));
		AndroidTestCase.assertTrue(categories.contains("Go Find"));
		AndroidTestCase.assertTrue(categories.contains("Bark Alert"));
		AndroidTestCase.assertTrue(categories.size() == 3);	
	}

}