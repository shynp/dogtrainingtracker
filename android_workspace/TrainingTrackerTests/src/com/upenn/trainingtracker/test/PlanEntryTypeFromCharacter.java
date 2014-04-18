package com.upenn.trainingtracker.test;

import junit.framework.Assert;

import com.upenn.trainingtracker.PlanEntry;

import android.test.AndroidTestCase;

public class PlanEntryTypeFromCharacter extends AndroidTestCase 
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
	public void testGetC() throws Throwable
	{
		AndroidTestCase.assertEquals(PlanEntry.typeFromCharacter('C'), PlanEntry.Type.CHECKBOX);
	}
	public void testGetO() throws Throwable
	{
		AndroidTestCase.assertEquals(PlanEntry.typeFromCharacter('O'), PlanEntry.Type.OPTIONS);
	}
	public void testGetI() throws Throwable
	{
		AndroidTestCase.assertEquals(PlanEntry.typeFromCharacter('I'), PlanEntry.Type.IMAGE_OPTIONS);
	}
	public void testGetFail() throws Throwable 
	{
		try{
			PlanEntry.typeFromCharacter('f');
			AndroidTestCase.fail();
		}
		catch (IllegalArgumentException e){
		}
	}

}