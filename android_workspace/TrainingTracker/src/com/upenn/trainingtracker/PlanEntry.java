package com.upenn.trainingtracker;

import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class PlanEntry 
{

	private String name;
	private String[] options;
	private String[] optionKeys;
	
	private PlanEntry.Type type;
	private String nameKey;

	
	public static enum Type {
		CHECKBOX, OPTIONS, IMAGE_OPTIONS
	}
	// nameKey is the value used in constructing the "plan" that is stored in the database
	
	public String getNameKey() {
		return nameKey;
	}
	public void setOptionKeys(String[] optionKeys) {
		this.optionKeys = optionKeys;
	}
	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getOptions() {
		return options;
	}
	public void setOptions(String[] options) {
		this.options = options;
	}
	public PlanEntry.Type getType() {
		return type;
	}
	public void setType(PlanEntry.Type type) {
		this.type = type;
	}
	public PlanEntry(String name, String nameKey, PlanEntry.Type type, String[] options, String[] optionKeys)
	{
		this.name = name;
		this.nameKey = name;
		this.options = options;
		this.optionKeys = optionKeys;
		this.type = type;
	}
	public PlanEntry(String name, String nameKey, char type, String[] options, String[] optionKeys)
	{
		this.name = name;
		this.nameKey = nameKey;
		this.options = options;
		this.optionKeys = optionKeys;
		this.type = this.typeFromCharacter(type);	
	}
	public PlanEntry(String name, String nameKey, PlanEntry.Type type)
	{
		if (type != PlanEntry.Type.CHECKBOX) 
		{
			throw new IllegalArgumentException("String array needs to be supplied to construction unless type is CHECKBOX");
		}
		this.type = type;
		this.name = name;
		this.nameKey = nameKey;
	}
	public PlanEntry(String name, String nameKey, char type)
	{
		PlanEntry.Type typeC = PlanEntry.typeFromCharacter(type);
		if (typeC != PlanEntry.Type.CHECKBOX) 
		{
			throw new IllegalArgumentException("String array needs to be supplied to construction unless type is CHECKBOX");
		}
		this.type = typeC;
		this.name = name;
		this.nameKey = nameKey;
	}
	public static PlanEntry.Type typeFromCharacter(char character)
	{
		switch (character) 
		{
		case 'C': return PlanEntry.Type.CHECKBOX;
		case 'O': return PlanEntry.Type.OPTIONS;
		case 'I': return PlanEntry.Type.IMAGE_OPTIONS;
		default: throw new IllegalArgumentException("Character did not match any cases: " + character);
		}
	}
	public String[] getOptionKeys()
	{
		return this.optionKeys;
	}
	@Override
	public boolean equals(Object object)
	{
		PlanEntry otherEntry = (PlanEntry) object;
		if (otherEntry.getType() != this.getType()) return false;

		if (this.optionKeys == null)
		{
			Log.i("TAG","object is null");
		}
		if (!this.nameKey.equals(otherEntry.getNameKey())) return false;
		// If they are both checkboxes and names match then return true
		if (otherEntry.getType() == PlanEntry.Type.CHECKBOX)
		{
			return true;
		}
		
		List<String> l1 = Arrays.asList(this.optionKeys);
		List<String> l2 = Arrays.asList(otherEntry.getOptionKeys());		
		for (String str : l1)
		{
			if (!l2.contains(str)) return false;
		}
		for (String str : l2)
		{
			if (!l1.contains(str)) return false;
		}
		return true;
	}

}
