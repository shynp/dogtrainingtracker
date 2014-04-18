package com.upenn.trainingtracker;

import java.util.ArrayList;

public class TrainingParameter {

	private String name;
	private ArrayList<String> options;
	
	public TrainingParameter(String name, ArrayList<String> options){
		this.name = name;
		this.options = options;
	}
	
	public String getName(){
		return name;
	}
	
	public ArrayList<String> getOptions(){
		return options;
	}
	
}
