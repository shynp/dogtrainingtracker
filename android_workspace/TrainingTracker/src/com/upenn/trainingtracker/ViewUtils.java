package com.upenn.trainingtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

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
    public void showAlertMessage(Context activity, String message)
    {
    	this.showAlertMessage(activity, message, "Ok");
    }
    public void showAlertMessage(Context activity, String message, String cancelMessage)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	builder.setMessage(message);
    	builder.setPositiveButton(cancelMessage, null);
    	builder.create().show();
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
