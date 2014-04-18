package com.upenn.trainingtracker.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CheckOutProgressView extends View
{
	private int numViews;
	private int circleRadius = 10;
	private int circleSpacing = 17;
	private int paddingLeft;
	private int paddingRight;
	private int height = 35;
	private boolean initialized = false;
	
	private int selectedIndex = 0;
	private Paint paintUnselected;
	private Paint paintSelected;
	
	public CheckOutProgressView(Context context) 
	{
		super(context);
	}
	public CheckOutProgressView(Context context, AttributeSet as) 
	{
		super(context, as);
	}
	public void initializeCheckOutProgressView(int numViews)
	{
		this.numViews = numViews;
	}
	private void initializeSizes()
	{
	    View view = (View) this.getParent();
	    int parentWidth = view.getWidth();		
	    Log.i("TAG","Parent width: " + parentWidth);
		int viewWidth = (this.circleRadius * 2) * numViews + (numViews - 1) * this.circleSpacing;
		this.paddingLeft = (parentWidth - viewWidth) / 2;
		this.paddingRight = this.paddingLeft;
		
		paintUnselected = new Paint();
		paintUnselected.setColor(Color.GRAY);
		paintUnselected.setStyle(Paint.Style.STROKE);
		paintUnselected.setStrokeWidth(5);

		paintSelected = new Paint();
		paintSelected.setColor(Color.GRAY);
		paintSelected.setStyle(Paint.Style.FILL_AND_STROKE);
		paintUnselected.setStrokeWidth(5);
	}
	public void onDraw(Canvas c) 
	{	
		if (!this.initialized)
		{
			this.initializeSizes();
		}
	    

		
		for (int index = 0; index < this.numViews; ++index)
		{
			int xLoc = this.paddingLeft + this.circleRadius + index * ((2 * this.circleRadius) + this.circleSpacing);
			Log.i("TAG",Integer.toString(xLoc));
			if (index == this.selectedIndex)
			{
				c.drawCircle(xLoc, this.height/2, this.circleRadius + 2, this.paintSelected);
			}
			else
			{
				c.drawCircle(xLoc, this.height/2, this.circleRadius, this.paintUnselected);	
			}
		}
	}
	public void setSelected(int viewNumber)
	{
		this.selectedIndex = viewNumber;
		this.invalidate();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int height = getMeasuredHeight();

	    setMeasuredDimension(parentWidth, this.height);
	}
}
