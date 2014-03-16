package com.upenn.trainingtracker;

import android.graphics.Bitmap;
import android.util.Log;

public class DogProfile
{
	private int ID;
	private String name;
	private String skillsTableName;
	private String birthDate;
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

	public String getBirthDate() {
		return birthDate;
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
