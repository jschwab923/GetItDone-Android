package com.iamnerdandsocanyou.getitdone;

import java.util.Calendar;

public class Task {
	
	String taskText;
	Calendar dateTime;
	String reminder;
	String proof;
	int points;
	
	long id;
	
	public Task(String taskText, Calendar dateTime, String reminder, String proof, int points, long id) {
		this.taskText = taskText;
		this.dateTime = dateTime;
		this.reminder = reminder;
		this.proof = proof;
		this.points = points;
		this.id = id;
	}
	
	public String toString() {
		return taskText;
	}
}
