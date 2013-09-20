package com.iamnerdandsocanyou.getitdone;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

// ****KEEP IN MIND LIST***//
//- Keep working on Life-cycle stuff, releasing resources, saving info etc -

//****TO DO LIST****//
// - Finish working on custom ArrayAdapter - 
// - Work on some UI fixes and start figuring out how to design the "Task done" process -

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	
	/**
	 * Object for handling all "Model" activities, retrieving task
	 * data, adding new tasks to the database etc. Will be used 
	 * throughout every class in the app that needs to do task 
	 * actions.
	 */
	private TaskManager taskManager;
			
	// Constants to represent the three different tabs.
	// private final int FIRST_PAGE = 0;
	// private final int SECOND_PAGE = 1;
	// private final int THIRD_PAGE = 2;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get a reference to the title bar TextView
//		final int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
//		final TextView title = (TextView)getWindow().findViewById(actionBarTitle);


		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab, and update the page content based on selected tab. 
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		
		
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		taskManager = TaskManager.getInstance();
		taskManager.setup(this);
		
		SharedPreferences startupInfo = getSharedPreferences(PrefsStrings.PREFS_NAME, 0);
		if(!startupInfo.contains(PrefsStrings.FIRST_STARTUP_DONE)) {		
			SharedPreferences.Editor prefsEditor = startupInfo.edit();
			prefsEditor.putBoolean(PrefsStrings.FIRST_STARTUP_DONE, true);
			prefsEditor.commit();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);	
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case(R.id.action_settings):
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	@Override
	public void onStop() {
		taskManager.saveTaskList(this);
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		taskManager.releaseResources();
		taskManager.saveTaskList(this);
		super.onDestroy();
	}
	
	/**
	* Directs all button clicks to the appropriate method
	*/
	public void clickListener(View v) {
		switch(v.getId()) {
		case(R.id.addTaskButton):
			Intent addTaskIntent = new Intent(this, AddTaskActivity.class);
			startActivity(addTaskIntent);
			break;
		case(R.id.statsButton):
			taskManager.getStats();
			break;
		case(R.id.tipTextView):
			changeTip(v);
			break;
		case(R.id.action_settings):
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
		}
	}
		
	private void changeTip(View tipText) {
		String newTip = taskManager.getTip();
		TextView tipTextView = (TextView)tipText;
		tipTextView.setText(newTip);
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return the correct fragment based on current selected actionBar position
			// (defined as a static inner class below) 
			
			Fragment fragment;
			switch(position) {
			case 0:
				fragment = new NowFragment();
				break;
			case 1:
				fragment = new UpcomingFragment();
				break;
			case 2:
				fragment = new ProductivityFragment();
				break;
			default:
				fragment = new NowFragment();
			}
			Bundle args = new Bundle();
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
		
	/**
	 * A fragment representing the 'Now' tab.
	 */
	public static class NowFragment extends Fragment {
		
		private static View rootView;
		private TextView currentTaskTextView;
		private TaskManager taskManager;
		
		private Typeface customFontLight;
		private Typeface customFontRegular;
		
		public NowFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {			
			rootView = inflater.inflate(R.layout.now,
					container, false);
			
	    	customFontLight = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.custom_font_light));
	    	customFontRegular = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.custom_font_regular));
			
			TextView titleTextView = (TextView)rootView.findViewById(R.id.currentTaskTitleTextView);
			titleTextView.setTypeface(customFontRegular);
			
			currentTaskTextView = (TextView)rootView.findViewById(R.id.currentTaskTextView);
			currentTaskTextView.setTypeface(customFontLight);
			
			TextView tipTextView = (TextView)rootView.findViewById(R.id.tipTextView);
			tipTextView.setTypeface(customFontLight);
			
			Button doneButton = (Button)rootView.findViewById(R.id.doneButton);
			doneButton.setTypeface(customFontLight);
			
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
		}
		
		@Override
		public void onResume() {
			super.onResume();
			loadContent();
		}
		
		@Override
		public void onStop() {
			super.onStop();
		}
		
		private void loadContent() {
			if(taskManager == null) {
				taskManager = TaskManager.getInstance();
			}
			if (currentTaskTextView == null) {
				currentTaskTextView = (TextView)rootView.findViewById(R.id.currentTaskTextView);
			}
			currentTaskTextView.setText(taskManager.getCurrentTask(rootView.getContext()).toString());
		}	
	}
	
	/**
	 * A fragment representing the 'Upcoming' tab.
	 */
	public static class UpcomingFragment extends Fragment {
		
		// Request codes for launching sub-activities.
		private final int ADD_TASK_CODE = 1;
		
		private TaskManager taskManager;
		private View rootView;
		private ListView upcomingTasks;
		private ArrayList<Task> tasks;
		private ArrayAdapter<Task> tasksAdapter;
		
		private Typeface customFontLight;
		private Typeface customFontRegular;
		
		public UpcomingFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {			
			rootView = inflater.inflate(R.layout.upcoming,
					container, false);
			
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			customFontLight = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.custom_font_light));
	    	customFontRegular = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.custom_font_regular));
	    	
	    	TextView titleTextView = (TextView)rootView.findViewById(R.id.upcomingTasksTextView);
	    	titleTextView.setTypeface(customFontRegular);
	    }
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
		}
		
		@Override
		public void onResume() {
			super.onResume();
			loadContent();
		}
		
		@Override
		public void onStop() {
			super.onStop();
		}
		
		private void loadContent() {
			if (taskManager == null) {
				taskManager = TaskManager.getInstance();
			}
			
			tasks = taskManager.getAllTasks(rootView.getContext());
				
			tasksAdapter = new ArrayAdapter<Task>(rootView.getContext(), R.layout.task_textview, tasks);
					
			upcomingTasks = (ListView)rootView.findViewById(R.id.upcomingTasksListView);
			upcomingTasks.setAdapter(tasksAdapter);
			
//			TextView taskListTextView = (TextView)rootView.findViewById(R.layout.task_textview);
//	    	taskListTextView.setTypeface(customFontLight);
					
			tasksAdapter.notifyDataSetChanged();
				
			upcomingTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String clickedTasks = parent.getItemAtPosition(position).toString();
					if (clickedTasks.equals(getString(R.string.add_task))) {
						Intent addTaskIntent = new Intent(rootView.getContext(), AddTaskActivity.class);
						startActivityForResult(addTaskIntent, ADD_TASK_CODE);
					}			
				}
			});
		}
	}
	
	/**
	 * A fragment representing the 'Later' tab.
	 */
	public static class ProductivityFragment extends Fragment {
	
		public ProductivityFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {			
			View rootView = inflater.inflate(R.layout.info,
					container, false);
			return rootView;
		}
		
		@Override
		public void onStop() {
			super.onStop();
		}
	}
}
