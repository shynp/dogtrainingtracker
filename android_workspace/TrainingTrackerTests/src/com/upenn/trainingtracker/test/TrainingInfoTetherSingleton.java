package com.upenn.trainingtracker.test;

import junit.framework.Assert;

import com.upenn.trainingtracker.PlanEntry;
import com.upenn.trainingtracker.TrainingInfoTether;

import android.test.AndroidTestCase;

public class TrainingInfoTetherSingleton extends AndroidTestCase 
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
	public void testSingleton() throws Throwable
	{
		TrainingInfoTether instance1 = TrainingInfoTether.getInstance();
		TrainingInfoTether instance2 = TrainingInfoTether.getInstance();
		if(instance1 == null || instance2 == null){
			fail("an instance is null!");
		}
		if(instance1 != instance2){
			//pointer equality! they should be the same instance
			fail("pointers aren't equal!");
		}
	}
}