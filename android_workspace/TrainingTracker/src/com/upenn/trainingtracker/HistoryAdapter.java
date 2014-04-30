package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.upenn.trainingtracker.customviews.HistoryEntryWidget;
import com.upenn.trainingtracker.customviews.SessionCategoryWidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter
{
	private List<HistoryEntryWidget> selection = new ArrayList<HistoryEntryWidget>();
	private HistoryAdapter.SelectionType selectionType = SelectionType.UNITY;
	private List<String> lastFilterSet;
	
	public enum SelectionType
	{
		UNITY, INTERSECTION
	}
	public enum FilterType
	{
		CATEGORY_KEY, PARENT_KEY, USER_NAME, USER_FULL_NAME, RESULT, OTHER
	}
	
	private Map<String, List<TrainingSession>> catKeyToSessions;
	private Map<String, List<TrainingSession>> userNameToSessions;
	private List<TrainingSession> allSessions;
	
	/*
	 * Maps
	 */
	private Map<String, List<HistoryEntryWidget>> catKeyToWidgets = new HashMap<String, List<HistoryEntryWidget>>();
	private Map<String, List<HistoryEntryWidget>> catParentToWidgets = new HashMap<String, List<HistoryEntryWidget>>();
	private Map<String, List<HistoryEntryWidget>> userNameToWidgets = new HashMap<String, List<HistoryEntryWidget>>();
	private Map<String, List<HistoryEntryWidget>> fullNameToWidgets = new HashMap<String, List<HistoryEntryWidget>>();
	private List<HistoryEntryWidget> allWidgets = new ArrayList<HistoryEntryWidget>();
	
	private Map<HistoryEntryWidget.Type, List<HistoryEntryWidget>> typeToWidgets = new HashMap<HistoryEntryWidget.Type, List<HistoryEntryWidget>>();
	/*
	 * Lists to determine what type of filter is
	 */
	private List<String> userNames = new ArrayList<String>();
	private List<String> fullNames = new ArrayList<String>();
	private List<String> categories = new ArrayList<String>();
	private List<String> parentCats = new ArrayList<String>();

	public HistoryAdapter(Context context, List<TrainingSession> allSessions, Map<String, List<TrainingSession>> catKeyToSessions,
			Map<String, List<TrainingSession>> userNameToSessions)
	{
		super();
		this.catKeyToSessions = catKeyToSessions;
		this.userNameToSessions = userNameToSessions;
		this.allSessions = allSessions;
				
		this.initializeViews(context, allSessions);
		for (HistoryEntryWidget entry : allWidgets)
		{
			this.selection.add(entry);
		}
		TrainingReader reader = TrainingReader.getInstance(context);
		this.parentCats = reader.getParentCategories();

		this.categories = reader.getAllCategories();
		UserTether tether = UserTether.getInstance();
		this.userNames = tether.getUserNames(context);
		this.fullNames = tether.getUserFullNames(context);
		this.applyFilterStrings(new ArrayList<String>());
	}
	public void setSelectionType(HistoryAdapter.SelectionType type)
	{
		this.selectionType = type;
		if (lastFilterSet != null)
		{
			this.applyFilterStrings(this.lastFilterSet);
		}
	}
	public void initializeViews(Context context, List<TrainingSession> sessions)
	{		
		for (TrainingSession session : this.allSessions)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			HistoryEntryWidget profileWidget = (HistoryEntryWidget) inflater.inflate(R.layout.history_widget, null);
		    // Initialize---------	
			profileWidget.initializeView(session.getCategoryKey(), session.getPlanMap(), session.getDate());
			profileWidget.initializeSuccessFailureButtons(session.getResultSequence());
			
			TextView trainerName = (TextView) profileWidget.findViewById(R.id.trainerName);
			trainerName.setText(session.getTrainerName());
			
			TextView sessionDate = (TextView) profileWidget.findViewById(R.id.date);
			Log.i("TAG","DA DATE: " + session.getSessionDateString());
			sessionDate.setText(session.getSessionDateString());
			
			TextView textView = (TextView) profileWidget.findViewById(R.id.title);
			textView.setText(session.getCategory());
			session.setView(profileWidget);
			//--------
			this.addWidgetToMaps(session, profileWidget);
		}
	}
	public void addWidgetToMaps(TrainingSession session, HistoryEntryWidget profileWidget)
	{
		List<HistoryEntryWidget> widgetList = this.catKeyToWidgets.get(session.getCategoryKey());
		if (widgetList == null)
		{
			widgetList = new ArrayList<HistoryEntryWidget>();
			widgetList.add(profileWidget);
			this.catKeyToWidgets.put(session.getCategoryKey(), widgetList);
		}
		else
		{
			widgetList.add(profileWidget);
		}
		TrainingReader reader = TrainingReader.getInstance(null);
		String parentCat = reader.getParentCatFromChild(session.getCategoryKey());
		widgetList = this.catParentToWidgets.get(parentCat);
		if (widgetList == null)
		{
			widgetList = new ArrayList<HistoryEntryWidget>();
			widgetList.add(profileWidget);
			this.catParentToWidgets.put(parentCat, widgetList);
		}
		else
		{
			widgetList.add(profileWidget);
		}
		
		
		widgetList = this.fullNameToWidgets.get(session.getTrainerName());
		if (widgetList == null)
		{
			widgetList = new ArrayList<HistoryEntryWidget>();
			widgetList.add(profileWidget);
			this.fullNameToWidgets.put(session.getTrainerName(), widgetList);
		}
		else
		{
			widgetList.add(profileWidget);
		}
		widgetList = this.userNameToWidgets.get(session.getUserName());
		Log.i("TAG","HERE: " + session.getUserName());
		if (widgetList == null)
		{
			widgetList = new ArrayList<HistoryEntryWidget>();
			widgetList.add(profileWidget);
			this.userNameToWidgets.put(session.getUserName(), widgetList);
		}
		else
		{
			widgetList.add(profileWidget);
		}
		widgetList = this.typeToWidgets.get(profileWidget.getType());
		if (widgetList == null)
		{
			widgetList = new ArrayList<HistoryEntryWidget>();
			widgetList.add(profileWidget);
			this.typeToWidgets.put(profileWidget.getType(), widgetList);
		}
		else
		{
			widgetList.add(profileWidget);
		}
		this.allWidgets.add(profileWidget);
	}

	public void applyFilterStrings(List<String> filters)
	{
		this.lastFilterSet = filters;
		
		selection.clear();
		if (filters.isEmpty())
		{
			for (HistoryEntryWidget entry : this.allWidgets)
			{
				this.selection.add(entry);
			}
			this.sortChronologically();
			this.notifyDataSetChanged();
			return;
		}
		TrainingReader reader = TrainingReader.getInstance(null);
		// Get all the lists
		List<List<HistoryEntryWidget>> entryLists = new ArrayList<List<HistoryEntryWidget>>();
		boolean emptyListPresent = false;
		for (String filter : filters)
		{
			List<HistoryEntryWidget> list = this.getListForFilter(filter);
			if (list != null) 
			{
				entryLists.add(list);
			}
			else
			{
				emptyListPresent = true;
			}
		}
		// First get the set of all entries
		Set<HistoryEntryWidget> entrySet = new HashSet<HistoryEntryWidget>();

		for (List<HistoryEntryWidget> list : entryLists)
		{
			entrySet.addAll(list);
		}
		this.selection.addAll(entrySet);
		switch (this.selectionType)
		{
			case UNITY:
				break;
			case INTERSECTION:
				if (!emptyListPresent)
				{
					for (List<HistoryEntryWidget> list : entryLists)
					{
						selection.retainAll(list);
					}
				}
				else
				{
					selection.clear();
				}
				break;
		}

		this.notifyDataSetChanged();
		this.sortChronologically();
	}
	private void sortChronologically()
	{
		Collections.sort(this.selection);
	}
	private List<HistoryEntryWidget> getListForFilter(String filter)
	{
		TrainingReader reader = TrainingReader.getInstance(null);
		switch (this.getFilterType(filter))
		{
			case CATEGORY_KEY:
				String catKey = reader.categoryToCatKey(filter);
				return this.catKeyToWidgets.get(catKey);
			case PARENT_KEY:
				return this.catParentToWidgets.get(filter);
			case USER_NAME:
				return this.userNameToWidgets.get(filter);
			case USER_FULL_NAME:
				return this.fullNameToWidgets.get(filter);
			case RESULT:
				if (filter.equals("Passed"))
				{
					return typeToWidgets.get(HistoryEntryWidget.Type.Passed);
				}
				else if (filter.equals("Failed"))
				{
					return typeToWidgets.get(HistoryEntryWidget.Type.Failed);
				}
				else if (filter.equals("Aborted"))
				{
					return typeToWidgets.get(HistoryEntryWidget.Type.Aborted);
				}
				else if (filter.equals("Planned"))
				{
					return typeToWidgets.get(HistoryEntryWidget.Type.Planned);
				}
				break;
		}
		return null;
	}
	public HistoryAdapter.FilterType getFilterType(String filter)
	{
		if (userNames.contains(filter))
		{
			return HistoryAdapter.FilterType.USER_NAME;
		}
		else if (fullNames.contains(filter))
		{
			return HistoryAdapter.FilterType.USER_FULL_NAME;
		}
		else if (categories.contains(filter))
		{
			return HistoryAdapter.FilterType.CATEGORY_KEY;
		}
		else if (parentCats.contains(filter))
		{
			return HistoryAdapter.FilterType.PARENT_KEY;
		}
		else if (filter.equals("Passed") || filter.equals("Failed") ||
				filter.equals("Aborted") || filter.equals("Planned"))
		{
			return HistoryAdapter.FilterType.RESULT;
		}
		return HistoryAdapter.FilterType.OTHER;
	}
	/*public void filterByUserName(String userName)
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
		
	}*/
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
		return selection.get(position);
	}

}
