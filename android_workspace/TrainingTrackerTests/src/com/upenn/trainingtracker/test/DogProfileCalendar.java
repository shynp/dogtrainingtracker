package com.upenn.trainingtracker.test;

import java.util.Calendar;

import junit.framework.Assert;

import com.upenn.trainingtracker.DogProfile;

import android.test.AndroidTestCase;

public class DogProfileCalendar extends AndroidTestCase 
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
	public void testCalendar() throws Throwable
	{
		DogProfile prof = new DogProfile(0, null, null, "2014-10-18", null, null, null);
		Calendar cal = prof.getBirthDateCalendar();
		assertEquals(cal.get(Calendar.YEAR), 2014);
		assertEquals(cal.get(Calendar.MONTH), 10);
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), 18);
	}
}