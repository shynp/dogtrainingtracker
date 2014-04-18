package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter
{
	private List<TrainingSession> selection = new ArrayList<TrainingSession>();
	
	private Map<String, List<TrainingSession>> catKeyToSessions;
	private Map<String, List<TrainingSession>> userNameToSessions;
	private List<TrainingSession> allSessions;
	
	public HistoryAdapter(Context context, List<TrainingSession> allSessions, Map<String, List<TrainingSession>> catKeyToSessions,
			Map<String, List<TrainingSession>> userNameToSessions)
	{
		super();
		this.catKeyToSessions = catKeyToSessions;
		this.userNameToSessions = userNameToSessions;
		this.allSessions = allSessions;

		this.initializeViews(context, allSessions);
		
		this.selection = allSessions;
	}
	public void initializeViews(Context context, List<TrainingSession> sessions)
	{
		for (TrainingSession session : this.allSessions)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			RelativeLayout profileWidget = (RelativeLayout) inflater.inflate(R.layout.dog_profile_widget, null);
			
			TextView textView = (TextView) profileWidget.findViewById(R.id.dogNameTextID);
			textView.setText(session.getCategory());
			session.setView(profileWidget);
		}
	}
	public void filterByUserName(String userName)
	{
		
	}
	public void filterByName(String name)
	{
		// get the username and call filterByUserName
	}
	public void filterByCategoryKey(String categoryKey)
	{
		
	}
	public void filterByParentCategoryKey(String parentCategoryKey)
	{
		
	}
	public void clearFilters()
	{
		
	}
	
	@Override
	public int getCount() 
	{
		// TODO Auto-generated method stub
		return this.selection.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View arg1, ViewGroup arg2) 
	{
		return selection.get(position).getView();
	}

}
