package com.upenn.trainingtracker.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import com.upenn.trainingtracker.PlanEntry;
import com.upenn.trainingtracker.TrainingReader;

import android.test.AndroidTestCase;

public class TrainingReaderGetViewCompositionByCategory extends AndroidTestCase 
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
	public void testGetAGBalance() throws Throwable
	{
		ArrayList<PlanEntry> entries = reader.getViewCompositionByCategory("Balance");
		String[] options = {"None", "Next to K9", "From a distance"};
		String[] optionKeys = {"none", "next_to_k9", "from_a_distance"};
		AndroidTestCase.assertTrue(entries.contains(new PlanEntry("Handler","handler_position",'O' ,options, optionKeys)));
	}
	public void testGetDACBack() throws Throwable
	{
		ArrayList<PlanEntry> entries = reader.getViewCompositionByCategory("Back");
		String[] options = {"None", "From Sit", "From Stand", "From Down", "Ground Level", "From Object"};
		String[] optionKeys = {"none", "from_sit", "from_stand", "from_down", "ground_level", "from_object"};
		AndroidTestCase.assertTrue(entries.contains(new PlanEntry("K9 Position","k9_position",'O' ,options, optionKeys)));

	}
	public void testGetFODown() throws Throwable
	{
		ArrayList<PlanEntry> entries = reader.getViewCompositionByCategory("Down");
		String[] options = {"None", "Ground", "Unstable Surface", "Elevated", "In Motion"};
		String[] optionKeys = {"none", "ground", "unstable_surface", "elevated", "in_motion"};
		AndroidTestCase.assertTrue(entries.contains(new PlanEntry("K9 Position","k9_position",'O' ,options, optionKeys)));
	}
	public void testGetSearch() throws Throwable 
	{
		ArrayList<PlanEntry> entries = reader.getViewCompositionByCategory("Bark Alert");
		String[] options = {"None", "S", "M"};
		String[] optionKeys = {"none", "s", "m"};
		AndroidTestCase.assertTrue(entries.contains(new PlanEntry("Other Dogs","other_dogs",'O' ,options, optionKeys)));
	}

}