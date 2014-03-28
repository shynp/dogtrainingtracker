package com.upenn.trainingtracker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.Bitmap;
import android.util.Log;

public class DogProfile
{
	private int ID;
	private String name;
	private String skillsTableName;
	private String birthDate;
	private Calendar birthDateCalendar;
	private String breed;
	private String serviceType;
	private Bitmap image;
	
	public DogProfile(int ID, String name, String skillsTableName,
			String birthDate, String breed, String serviceType, Bitmap image) 
	{
		this.ID = ID;
		this.name = name;
		this.skillsTableName = skillsTableName;
		this.birthDate = birthDate;
		this.breed = breed;
		this.serviceType = serviceType;
		this.image = image;

		String[] parts = birthDate.split("-");
		int year = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int day = Integer.parseInt(parts[2]);
		
		Calendar cal = GregorianCalendar.getInstance();
		
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		this.birthDateCalendar = cal;
	}

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSkillsTableName() {
		return skillsTableName;
	}

	public void setSkillsTableName(String skillsTableName) {
		this.skillsTableName = skillsTableName;
	}

	public String getBirthDateString() {
		return birthDate;
	}
	public Calendar getBirthDateCalendar()
	{
		return this.birthDateCalendar;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getBreed() {
		return breed;
	}

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}


}
