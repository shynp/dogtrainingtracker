package com.upenn.trainingtracker;

import android.app.Activity;
import android.app.AlertDialog;

public class ViewUtils 
{
	private static ViewUtils instance;
	
	private ViewUtils()
	{
	}
	public static ViewUtils getInstance()
	{
		if (ViewUtils.instance == null)
		{
			ViewUtils.instance = new ViewUtils();
		}
		return ViewUtils.instance;
	}
    public void showAlertMessage(Activity activity, String message)
    {
    	this.showAlertMessage(activity, message, "Ok");
    }
    public void showAlertMessage(Activity activity, String message, String cancelMessage)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	builder.setMessage(message);
    	builder.setPositiveButton(cancelMessage, null);
    	builder.create().show();
    }
}
