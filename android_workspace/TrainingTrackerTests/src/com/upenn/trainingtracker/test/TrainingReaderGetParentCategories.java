package com.upenn.trainingtracker.test;

import java.util.List;

import junit.framework.Assert;

import com.upenn.trainingtracker.TrainingReader;

import android.test.AndroidTestCase;

public class TrainingReaderGetParentCategories extends AndroidTestCase 
{
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
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
		TrainingReader reader = TrainingReader.getInstance(this.getContext());
		List<String> categories = reader.getParentCategories();
		AndroidTestCase.assertTrue(categories.contains("Direciton and Control"));
	}
	public void testGetAG() throws Throwable
	{
		TrainingReader reader = TrainingReader.getInstance(this.getContext());
		List<String> categories = reader.getParentCategories();
		AndroidTestCase.assertTrue(categories.contains("Agility"));
	}
	public void testGetFOB() throws Throwable
	{
		TrainingReader reader = TrainingReader.getInstance(this.getContext());
		List<String> categories = reader.getParentCategories();
		AndroidTestCase.assertTrue(categories.contains("Walk It Plank"));	
	}
	public void testGetSearch() throws Throwable 
	{
		TrainingReader reader = TrainingReader.getInstance(this.getContext());
		List<String> categories = reader.getParentCategories();
		AndroidTestCase.assertTrue(categories.contains("Search"));	
	}
	public void testCatSize() throws Throwable
	{
		TrainingReader reader = TrainingReader.getInstance(this.getContext());
		List<String> categories = reader.getParentCategories();
		AndroidTestCase.assertTrue(categories.size() == 4);	
	}	
}