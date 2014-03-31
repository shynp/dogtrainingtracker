package com.upenn.trainingtracker;

public class PlanEntry 
{
	public static enum Type {
		CHECKBOX, OPTIONS, IMAGE_OPTIONS
	}
	private String name;
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
	private String[] options;
	private PlanEntry.Type type;
	
	public PlanEntry(String name, PlanEntry.Type type, String[] options)
	{
		this.name = name;
		this.options = options;
		this.type = type;
	}
	public PlanEntry(String name, char type, String[] options)
	{
		this.name = name;
		this.options = options;
		this.type = this.typeFromCharacter(type);
	}
	public PlanEntry(String name, PlanEntry.Type type)
	{
		if (type != PlanEntry.Type.CHECKBOX) 
		{
			throw new IllegalArgumentException("String array needs to be supplied to construction unless type is CHECKBOX");
		}
		this.type = type;
	}
	public PlanEntry(String name, char type)
	{
		PlanEntry.Type typeC = PlanEntry.typeFromCharacter(type);
		if (typeC != PlanEntry.Type.CHECKBOX) 
		{
			throw new IllegalArgumentException("String array needs to be supplied to construction unless type is CHECKBOX");
		}
		this.type = typeC;
		this.name = name;
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

}
