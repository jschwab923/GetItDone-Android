package com.iamnerdandsocanyou.getitdone;

import java.util.Calendar;

public class Task implements Comparable<Task>{
	
	long id;
	String taskText;
	Calendar dateTime;
	String reminder;
	String proof;
	String category;
	String recurring;
	int points;
	int complete;
	
	public Task(String taskText, Calendar dateTime, String reminder, String proof, int points, String category, String recurring, long id) {
		this.taskText = taskText;
		this.dateTime = dateTime;
		this.reminder = reminder;
		this.proof = proof;
		this.points = points;
		this.category = category;
		this.recurring = recurring;
		this.id = id;
	}
	
	public String toString() {
		return taskText;
	}

	@Override
	public int compareTo(Task another) {
		return this.dateTime.compareTo(another.dateTime);
	}


}
