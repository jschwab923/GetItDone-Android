package com.iamnerdandsocanyou.getitdone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseContract extends SQLiteOpenHelper implements Runnable {
	
	private final static int DATABASE_VERSION = 1;
	private final static String DATABASE_NAME = "getItDoneDB.db";
	
	// Table name
	private final static String TABLE_TASKS = "tasks";
	
	// Column names
	private final static String KEY_ID = "id";
	private final static String KEY_YEAR = "year";
	private final static String KEY_MONTH = "month";
	private final static String KEY_DAY = "day";
	private final static String KEY_HOUR = "hour";
	private final static String KEY_MINUTE = "minute";
	private final static String KEY_REMINDER = "reminder";
	private final static String KEY_PROOF = "proof";
	private final static String KEY_POINTS = "points";
	private final static String KEY_TEXT = "text";
	private final static String KEY_COMPLETE = "complete";
	
	@Override
	public void run() {
	}
		
	public DatabaseContract(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase taskDB) {
		 String CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
	                + KEY_ID + " INTEGER PRIMARY KEY,"
	                + KEY_YEAR + " INTEGER NOT NULL," 
	                + KEY_MONTH + " INTEGER NOT NULL,"   
	                + KEY_DAY + " INTEGER NOT NULL,"
	                + KEY_HOUR + " INTEGER NOT NULL,"
	                + KEY_MINUTE + " INTEGER NOT NULL,"
	                + KEY_REMINDER + " TEXT NOT NULL,"
	                + KEY_PROOF + " TEXT NOT NULL,"
	                + KEY_POINTS + " INTEGER NOT NULL,"
	                + KEY_TEXT + " TEXT NOT NULL,"
	                + KEY_COMPLETE + " INTEGER NOT NULL"
	                + ")";
		 
	        taskDB.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
 
        // Create tables again
        onCreate(db);
	}
	
	/**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Add new Task
    public void addTaskList(List<Task> taskList, int newTasks) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        for (int i = 0; i < newTasks; i++ ) {
        	Task task = taskList.get(i);
        	if (task.id != 1) {
        		addTask(task);  	
        	}
        }
        db.close(); // Closing database connection
    }

    public void addTask(Task task) {
    	SQLiteDatabase db = this.getWritableDatabase();

    	Calendar currentDate = task.dateTime;

    	ContentValues newValues = new ContentValues();
    	newValues.put(KEY_ID, task.id);
    	newValues.put(KEY_YEAR, currentDate.get(Calendar.YEAR));
    	newValues.put(KEY_MONTH, currentDate.get(Calendar.MONTH));
    	newValues.put(KEY_DAY, currentDate.get(Calendar.DAY_OF_MONTH));
    	newValues.put(KEY_HOUR, currentDate.get(Calendar.HOUR_OF_DAY));
    	newValues.put(KEY_MINUTE, currentDate.get(Calendar.MINUTE));
    	newValues.put(KEY_REMINDER, task.reminder);
    	newValues.put(KEY_PROOF, task.proof);
    	newValues.put(KEY_POINTS, task.points);
    	newValues.put(KEY_TEXT, task.taskText);
    	newValues.put(KEY_COMPLETE, false);
    	// Inserting Row
    	db.insert(TABLE_TASKS, null, newValues);
    	// Only happens when adding the defaultTask on first startup
    	if (task.id == 1) {
    		db.close(); // Closing database connection
    	}
    }
 
    // Get soonest task
    public Task getSoonestTask(long taskId, Context context) {
        Task task;
       
        if (taskId == 1) {
	    	Calendar dateTime = new GregorianCalendar();
	    	
	    	String reminder = "none";
	    	String proof = "none";
	    	int points = 1;
	    	String taskText = context.getString(R.string.add_task); 
	    	
	    	task = new Task(taskText, dateTime, reminder, proof, points, taskId);
        } else {
        	SQLiteDatabase db = this.getReadableDatabase();
        	Cursor cursor = db.query(TABLE_TASKS, null, KEY_ID + "=?", new String[] {""+taskId}, null, null, null);
        	cursor.moveToFirst();
        	int year = cursor.getInt(1);
        	int month = cursor.getInt(2);
        	int day = cursor.getInt(3);
        	int hour = cursor.getInt(4);
        	int minute = cursor.getInt(5);
        	Calendar dateTime = new GregorianCalendar(year, month, day, hour, minute);

        	String reminder = cursor.getString(6);
        	String proof = cursor.getString(7);
        	int points = cursor.getInt(8);
        	String taskText = cursor.getString(9); 

        	task = new Task(taskText, dateTime, reminder, proof, points, taskId);
        }
        return task;
    }
     
    // Get list of all Tasks
    public List<Task> getAllTasks() {
        List<Task> TaskList = new ArrayList<Task>();
        
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	int id = cursor.getInt(0);
            	
            	int year = cursor.getInt(1);
            	int month = cursor.getInt(2);
            	int day = cursor.getInt(3);
            	int hour = cursor.getInt(4);
            	int minute = cursor.getInt(5);
            	Calendar dateTime = new GregorianCalendar(year, month, day, hour, minute);
            	
            	String reminder = cursor.getString(6);
            	String proof = cursor.getString(7);
            	int points = cursor.getInt(8);
            	String taskText = cursor.getString(9); 
            	
            	Task task = new Task(taskText, dateTime, reminder, proof, points, id);
      
                TaskList.add(0, task);
            } while (cursor.moveToNext());
        }
 
        return TaskList;
    }
 
    // Update a single Task
    public int updateTask(Task taskToUpdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        Calendar newDate = taskToUpdate.dateTime;
 
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_YEAR, newDate.get(Calendar.YEAR));
        newValues.put(KEY_MONTH, newDate.get(Calendar.MONTH));
        newValues.put(KEY_DAY, newDate.get(Calendar.DAY_OF_MONTH));
        newValues.put(KEY_HOUR, newDate.get(Calendar.HOUR_OF_DAY));
        newValues.put(KEY_MINUTE, newDate.get(Calendar.MINUTE));
        newValues.put(KEY_REMINDER, taskToUpdate.reminder);
        newValues.put(KEY_PROOF, taskToUpdate.proof);
        newValues.put(KEY_POINTS, taskToUpdate.points);
        newValues.put(KEY_TEXT, taskToUpdate.taskText);
        
        // updating row
        return db.update(TABLE_TASKS, newValues, KEY_ID + "=?",
                new String[] { String.valueOf(taskToUpdate.id) });
    }
 
    // Delete a single Task
    public void deleteTask(Task taskToDelete) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + " = ?",
                new String[] { String.valueOf(taskToDelete.id) });
        db.close();
    }
 
    // Mark task as done in database
    public void taskDone(long id) {
    	String where = KEY_ID + "=" + id;
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.update(TABLE_TASKS, null , where, null);
    }
}
