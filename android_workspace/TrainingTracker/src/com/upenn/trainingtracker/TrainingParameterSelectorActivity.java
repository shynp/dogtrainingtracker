package com.upenn.trainingtracker;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

public class TrainingParameterSelectorActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.training_param_selector_layout);
    }
	
	private void createButtons(ArrayList<TrainingParameter> list){
		
	}
}
