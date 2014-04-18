package com.upenn.trainingtracker.customviews;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.upenn.trainingtracker.R;
import com.upenn.trainingtracker.customviews.FlowLayout.LayoutParams;

public class TagButton extends Button
{

	public TagButton(Context context, String text) 
	{
		super(context);
		this.setTextSize(12);
		this.setBackgroundResource(R.drawable.tag_button);
		this.setTextColor(Color.WHITE);
		
		
		this.setText(text);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, 90);
		params.setMargins(5, 5);
		this.setLayoutParams(params);
		
		final TagButton buttonF = this;
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				FlowLayout flow = (FlowLayout) buttonF.getParent();
				flow.removeView(buttonF);
			}
		});
	}

}
