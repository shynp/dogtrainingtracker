package com.upenn.trainingtracker.test;

import java.util.List;

import junit.framework.Assert;

import com.upenn.trainingtracker.TrainingReader;

import android.test.AndroidTestCase;

public class TrainingReaderGetAllCategories extends AndroidTestCase 
{
	private TrainingReader reader;
	private List<String> categories;
	@Override
	protected void setUp() throws Exception 
	{
		super.setUp();
		reader = TrainingReader.getInstance(this.getContext());
		categories = reader.getAllCategories();
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
	public void testGetGO() throws Throwable
	{
		AndroidTestCase.assertTrue(categories.contains("Go Out"));
	}
	public void testGetPatterns() throws Throwable
	{
		AndroidTestCase.assertTrue(categories.contains("Patterns"));
	}
	public void testGetHAO() throws Throwable
	{
		AndroidTestCase.assertTrue(categories.contains("Hup and Off"));	
	}
	public void testGetOver() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Over"));	
	}
	public void testGetBack() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Back"));	
	}
	public void testGetWIP() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Walk It Plank"));	
	}
	public void testGetTunnel() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Tunnel"));	
	}
	public void testGetWIS() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Walk It Surface"));	
	}
	public void testGetClimb() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Climb"));	
	}
	public void testGetCrawl() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Crawl"));	
	}
	public void testGetBalance() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Balance"));	
	}
	public void testGetStay() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Stay"));	
	}
	public void testGetSitStay() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Sit / Stay"));	
	}
	public void testGetCR() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Come - Recall"));	
	}
	public void testGetDown() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Down"));	
	}
	public void testGetHeel() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Heel"));	
	}
	public void testGetDS() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Down / Stay"));	
	}
	public void testGetSearch() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Search"));	
	}
	public void testGetGF() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Go Find"));	
	}
	public void testGetBA() throws Throwable 
	{
		AndroidTestCase.assertTrue(categories.contains("Bark Alert"));	
	}
	public void testCatSize() throws Throwable
	{
		AndroidTestCase.assertTrue(categories.size() == 20);	
	}	
}