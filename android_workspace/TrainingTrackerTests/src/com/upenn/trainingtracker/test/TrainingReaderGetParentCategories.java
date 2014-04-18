package com.upenn.trainingtracker.test;

import java.util.List;

import junit.framework.Assert;

import com.upenn.trainingtracker.TrainingReader;

import android.test.AndroidTestCase;

public class TrainingReaderGetParentCategories extends AndroidTestCase 
{
	private TrainingReader reader;
	private List<String> categories;
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		reader = TrainingReader.getInstance(this.getContext());
		categories = reader.getParentCategories();
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
		AndroidTestCase.assertTrue(categories.contains("Direction and Control"));
	}
	public void testGetAG() throws Throwable
	{
		AndroidTestCase.assertTrue(categories.contains("Agility"));
	}
	public void testGetFOB() throws Throwable
	{
		AndroidTestCase.assertTrue(categories.contains("Foundation Obedience"));	
	}
	public void testGetSearch() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Search"));	
	}
	public void testCatSize() throws Throwable
	{
		AndroidTestCase.assertTrue(categories.size() == 4);	
	}	
}