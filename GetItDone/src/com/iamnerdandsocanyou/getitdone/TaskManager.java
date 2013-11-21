package com.iamnerdandsocanyou.getitdone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

final public class TaskManager {
	
	private static TaskManager taskManager = null;
	
	private static ArrayList<Task> allTasks;
	
	private static DatabaseContract dbManager;
	
	private Task currentTask;

	private TaskManager() {
	}
	
	public static TaskManager getInstance() {
		if (taskManager == null) {
			taskManager = new TaskManager();
		}
		return taskManager;
	}
	
	public void setup(Context context) {
		// Check to see if first startup has already happened
		SharedPreferences prefs = context.getSharedPreferences(PrefsStrings.PREFS_NAME, 0);
		if ((prefs.contains(PrefsStrings.FIRST_STARTUP_DONE))) {
			if (dbManager == null) {
				dbManager = new DatabaseContract(context);
				Thread dbThread = new Thread(dbManager);
				dbThread.start();
			}
			allTasks = (ArrayList<Task>)dbManager.getAllTasks();
			if(allTasks.size() > 1) {
				currentTask = dbManager.getSoonestTask(context);
			} else {
				if (allTasks.size() == 1) {
					currentTask = allTasks.get(0);
				}
			}
		} 
	}
	
	/** Returns an array containing all current, not done, tasks that 
	 * are saved in the SQLite database.
	 * 
	 * @return allTasks
	 */
	public ArrayList<Task> getAllTasks(Context context) {
		if (allTasks == null) {
			if (dbManager == null) {
				dbManager = new DatabaseContract(context);
				new Thread(dbManager).start();	
			}
			allTasks = (ArrayList<Task>)dbManager.getAllTasks();
		}
		return allTasks;
	}
	
	/** Returns just the task with the most recent scheduled completion time.
	 * 
	 * @return currentTask
	 */
	public Task getCurrentTask(Context context) {
		return currentTask;
	}
	
	/** Adds a task and all it's associated information to the allTask ArrayList.
	 *  Updates the count of new tasks to be eventually written to database. 
	 * @param taskInfo
	 */
	public boolean addTask(Task newTask, Context context) {
		if(dbManager == null) {
			dbManager = new DatabaseContract(context);
			new Thread(dbManager).start();
		}
		
		SharedPreferences startupInfo = context.getSharedPreferences(PrefsStrings.PREFS_NAME, 0);
		SharedPreferences.Editor prefsEditor = startupInfo.edit();
		// Check SharedPreferences to see if no tasks have been added, or if current task count is 0.
		if (!startupInfo.contains(PrefsStrings.TASKS_ADDED)) {
			// If so, update SharedPreferences to indicate that one task has been added
			// and save its taskId as the SOONEST_TASK in SharedPreferences
			// and start NEW_TASKS count.
			prefsEditor.putLong(PrefsStrings.SOONEST_TASK, newTask.id)
					.putInt(PrefsStrings.TASKS_ADDED, 1)
					.putInt(PrefsStrings.NEW_TASKS, 0)
					.commit();
			
			currentTask = newTask;
			
			allTasks = new ArrayList<Task>();
		} else {
			// Check to see if task being added has a sooner date than the current soonest task.
			if (newTask.dateTime.compareTo(dbManager.getSoonestTask(context).dateTime) < 0) {
				// If so update the SOONEST_TASK taskId in sharedPreferences and reassign the 
				// currentTask member variable.
				prefsEditor.putLong(PrefsStrings.SOONEST_TASK, newTask.id);		
				currentTask = newTask;
			}
			
			// Increment the TASKS_ADDED count in SharedPreferences
			int currentTaskCount = startupInfo.getInt(PrefsStrings.TASKS_ADDED, 1);
			prefsEditor.putInt(PrefsStrings.TASKS_ADDED, currentTaskCount + 1).commit();
		}
		
		allTasks.add(0, newTask);
		
		// Update that there is another task in the allTasks list that hasn't yet been 
		// written to the database. 
		prefsEditor.putInt(PrefsStrings.NEW_TASKS, startupInfo.getInt(PrefsStrings.NEW_TASKS, 1) + 1)
				   .commit();
		
		if (allTasks.size() >= 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 *  Marks a task as done in the SQLite database and updates stats.
	 */
	public void taskDone(long id, Context context) {
		if(dbManager == null) {
			dbManager = new DatabaseContract(context);
			new Thread(dbManager).start();
		}
		dbManager.taskDone(id);
	}
	
	/**
	 * Updates one specific task
	 */
	public Boolean updateTask(Task taskToUpdate, Context context) {
		if(dbManager == null) {
			dbManager = new DatabaseContract(context);
			new Thread(dbManager).start();
		}
		dbManager.updateTask(taskToUpdate);
		
		allTasks = (ArrayList<Task>)dbManager.getAllTasks();
		
		currentTask = dbManager.getSoonestTask(context);
		return true;
	}
	
	/**Returns info about tasks completed and points earned over time.
	 * The value String represents two integers, tasks completed, and 
	 * points earned, separated by a space. 
	 * 
	 * @param getStats
	 * @return statInfo
	 */
	public Map<String, String> getStats() {
		Map<String, String> statInfo = new HashMap<String, String>();
		return statInfo;
	}
	
	public String getTip() {
		String defaultTip = "Work smarter AND harder";
		String newTip = defaultTip;
		return newTip;
	}
	
	public void saveTaskList(Context context) {
		if(dbManager == null) {
			dbManager = new DatabaseContract(context);
			new Thread(dbManager).start();
		}
		
		if (allTasks.size() != 0) {
			SharedPreferences prefs = context.getSharedPreferences(PrefsStrings.PREFS_NAME, 0);
			int newTasks = prefs.getInt(PrefsStrings.NEW_TASKS, 0);
			dbManager.addTaskList(allTasks, newTasks);
			
			// Update the NEW_TASKS count to indicate that all tasks have 
			// been written to the database.
			prefs.edit().putInt(PrefsStrings.NEW_TASKS, 0).commit();
		}
		
	}
	
	public void releaseResources() {
		dbManager = null;
	}
}
