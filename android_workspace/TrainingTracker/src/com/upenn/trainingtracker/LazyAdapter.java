package com.upenn.trainingtracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter implements Filterable
{
	private ArrayList<DogProfile> profiles = new ArrayList<DogProfile>();
	private LayoutInflater inflater;
	private ProfileComparator comparator;
	
	public LazyAdapter(Context context, ArrayList<DogProfile> profiles)
	{
		super();
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.profiles = profiles;
		comparator = new ProfileComparator();
	}
	private class ProfileComparator implements Comparator<DogProfile>
	{
		private String constraint;
		@Override
		public int compare(DogProfile profile1, DogProfile profile2) 
		{
			String name1 = profile1.getName();
			String name2 = profile2.getName();
			if (constraint.trim().equals(""))
			{
				return name1.compareTo(name2);
			}
			// If beginning exact match, then this ordering takes priority
			boolean firstStarts = name1.toLowerCase().startsWith(constraint);
			boolean secondStarts = name2.toLowerCase().startsWith(constraint);
			if (firstStarts && secondStarts)
			{
				return name1.compareTo(name2);
			}
			if (firstStarts && !secondStarts)
			{
				return -10;
			}
			if (!firstStarts && secondStarts)
			{
				return 10;
			}
			// Otherwise use levenshtein
			int distance1 = this.computeLevenshteinDistance(name1, constraint);
			int distance2 = this.computeLevenshteinDistance(name2, constraint);
			
			if (distance1 < distance2) return -10;
			else if (distance1 > distance2) return 10;
			else return 0;
		}
		public void setConstraint(String constraint)
		{
			this.constraint = constraint.toLowerCase();
		}
		private int minimum(int a, int b, int c) {
			return Math.min(Math.min(a, b), c);
		}
		public int computeLevenshteinDistance(String str1,String str2) {
			int[][] distance = new int[str1.length() + 1][str2.length() + 1];
	 
			for (int i = 0; i <= str1.length(); i++)
				distance[i][0] = i;
			for (int j = 1; j <= str2.length(); j++)
				distance[0][j] = j;
	 
			for (int i = 1; i <= str1.length(); i++)
				for (int j = 1; j <= str2.length(); j++)
					distance[i][j] = minimum(
							distance[i - 1][j] + 1,
							distance[i][j - 1] + 1,
							distance[i - 1][j - 1]+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
	 
			return distance[str1.length()][str2.length()];    
		}
	}
	
	
	@Override
	public int getCount() 
	{
		return profiles.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		RelativeLayout profileWidget = (RelativeLayout) inflater.inflate(R.layout.dog_profile_widget, null);
		DogProfile profile = this.profiles.get(position);
		
		ImageView imageView = (ImageView) profileWidget.findViewById(R.id.profileImageID);
		imageView.setImageBitmap(profile.getImage());

		TextView textView = (TextView) profileWidget.findViewById(R.id.dogNameTextID);
		textView.setText(profile.getName());
		
		profileWidget.setId(profile.getID());
		return profileWidget;
	}

	@Override
	public Filter getFilter() 
	{
		Filter filter = new Filter(){
			@Override
			protected FilterResults performFiltering(CharSequence constraint) 
			{
				Log.i("TAG","Performing filtering");
				FilterResults results = new FilterResults();
				ArrayList<DogProfile> profilesFiltered = new ArrayList<DogProfile>();
				
				if (constraint == null)
				{
					results.count = LazyAdapter.this.profiles.size();
					results.values = LazyAdapter.this.profiles;
					return results;
				}
				Log.i("TAG","Filtering and returning");
				LazyAdapter.this.comparator.setConstraint(constraint.toString());
				Collections.sort(LazyAdapter.this.profiles, comparator);

				results.count = LazyAdapter.this.profiles.size();
				results.values = LazyAdapter.this.profiles;
				return results;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) 
			{
				LazyAdapter.this.profiles = (ArrayList<DogProfile>) results.values;
				LazyAdapter.this.notifyDataSetChanged();
			}
			
		};
		return filter;
	}

}
