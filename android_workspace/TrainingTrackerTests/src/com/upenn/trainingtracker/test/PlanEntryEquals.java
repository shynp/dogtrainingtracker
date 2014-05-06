package com.upenn.trainingtracker.test;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import com.upenn.trainingtracker.PlanEntry;
import com.upenn.trainingtracker.TrainingInfoTether;

import android.test.AndroidTestCase;

public class PlanEntryEquals extends AndroidTestCase 
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
	public void testCorrectCheckbox() throws Throwable
	{
		PlanEntry plan1 = new PlanEntry("name", "nameKeySame", 'C');
		PlanEntry plan2 = new PlanEntry("name", "nameKeySame", PlanEntry.Type.CHECKBOX);
		
		Assert.assertEquals(plan1, plan2);
	}
	public void testIncorrectCheckbox() throws Throwable
	{
		PlanEntry plan1 = new PlanEntry("name", "nameKeyDiff1", 'C');
		PlanEntry plan2 = new PlanEntry("name", "nameKeyDiff2", PlanEntry.Type.CHECKBOX);
		if(plan1.equals(plan2)){
			Assert.fail();
		}
	}
	
	public void testCorrectOther() throws Throwable
	{
		String[] options1 = {"option1","option2","option3"};
		String[] optionKeys1 = {"key1", "key2", "key3"};
		String[] options2 = {"option2", "option1", "option3"};
		String[] optionKeys2 = {"key2", "key1", "key3"};
		PlanEntry plan1 = new PlanEntry("name", "nameKeySame", 'O', options1, optionKeys1);
		PlanEntry plan2 = new PlanEntry("name", "nameKeySame", PlanEntry.Type.OPTIONS, options2, optionKeys2);
		
		Assert.assertEquals(plan1, plan2);
	}
	
	public void testIncorrectOther() throws Throwable
	{
		String[] options1 = {"option1","option2","option3"};
		String[] optionKeys1 = {"key1", "key2", "key3"};
		String[] options2 = {"diffoption2", "diffoption1", "diffoption3"};
		String[] optionKeys2 = {"diffkey2", "diffkey1", "diffkey3"};
		PlanEntry plan1 = new PlanEntry("name", "nameKeySame", PlanEntry.Type.OPTIONS, options1, optionKeys1);
		PlanEntry plan2 = new PlanEntry("name", "nameKeySame", PlanEntry.Type.OPTIONS, options2, optionKeys2);
		
		if(plan1.equals(plan2)){
			Assert.fail();
		}
	}
}