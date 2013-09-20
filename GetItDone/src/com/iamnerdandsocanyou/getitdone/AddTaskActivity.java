package com.iamnerdandsocanyou.getitdone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddTaskActivity extends Activity {
	
	private TextView dateTextView;
	private EditText taskEditText;
	private Spinner reminderSpinner; 
	private Spinner proofSpinner; 
	private Spinner pointSpinner;
	
	private static TaskManager taskManager = TaskManager.getInstance();
	
	private String formattedTimeDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);
		// Show the Up button in the action bar.
		setupActionBar();
		
		final Typeface customFontLight = Typeface.createFromAsset(getAssets(), getString(R.string.custom_font_light));
    	final Typeface customFontRegular = Typeface.createFromAsset(getAssets(), getString(R.string.custom_font_regular));
    	
    	TextView titleTextView = (TextView)findViewById(R.id.addTaskLabel);
    	titleTextView.setTypeface(customFontRegular);
    	
    	Button getItDoneButton = (Button)findViewById(R.id.submitTask);
    	getItDoneButton.setTypeface(customFontRegular);
		
		dateTextView = (TextView)findViewById(R.id.dateTextView);
		dateTextView.setTypeface(customFontLight);
		
		taskEditText = (EditText)findViewById(R.id.taskEditText);
		taskEditText.setTypeface(customFontLight);
		
		reminderSpinner = (Spinner)findViewById(R.id.reminderSpinner);
		proofSpinner = (Spinner)findViewById(R.id.proofSpinner);
		pointSpinner = (Spinner)findViewById(R.id.pointSpinner);
		
		CustomSpinnerArrayAdapter reminderAdapter = new CustomSpinnerArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.reminder_choices));
		reminderSpinner.setAdapter(reminderAdapter);
		
		CustomSpinnerArrayAdapter proofAdapter = new CustomSpinnerArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.proof_choices));
		proofSpinner.setAdapter(proofAdapter);
		
		CustomSpinnerArrayAdapter pointAdapter = new CustomSpinnerArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.point_choices));
		pointSpinner.setAdapter(pointAdapter);
		
		reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// An item was selected. You can retrieve the selected item using
				// parent.getItemAtPosition(pos)
				final int gently = 1;
				final int vigorously = 2;
				
				TextView reminderDescriptionTextView = (TextView)findViewById(R.id.reminderDescriptionTextView);
				reminderDescriptionTextView.setTypeface(customFontLight);
				
				switch(pos) {
				case(gently):
					reminderDescriptionTextView.setText(Html.fromHtml(getString(R.string.reminder_description_gently)));
					break;
				case(vigorously):
					reminderDescriptionTextView.setText(Html.fromHtml(getString(R.string.reminder_description_vigorously)));
					break;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_task_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (android.R.id.home):
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case (R.id.help):
			Dialog helpDialog = new Dialog(this);
			helpDialog.setContentView(R.layout.help_screen);
			helpDialog.setTitle("Help");
			helpDialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	public void dateChooserClickListener(View v) {
		final AlertDialog.Builder dateTimePickerDialog = new AlertDialog.Builder(this);
		final LayoutInflater dateTimePickerInflater = this.getLayoutInflater();
		
		final View dateTimeView = dateTimePickerInflater.inflate(R.layout.date_time_picker_dialog, null);
		final TimePicker timePicker = (TimePicker)dateTimeView.findViewById(R.id.timePicker);
		final DatePicker datePicker = (DatePicker)dateTimeView.findViewById(R.id.datePicker);
		
		dateTimePickerDialog.setView(dateTimeView);
		dateTimePickerDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
					Calendar chosenTimeDate = new GregorianCalendar();
					chosenTimeDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
							timePicker.getCurrentHour(), timePicker.getCurrentMinute());
					
					SimpleDateFormat timeDateFormatter = new SimpleDateFormat("MM/dd/yy h:mm a", Locale.getDefault());
					formattedTimeDate = timeDateFormatter.format(chosenTimeDate.getTime());
					
					dateTextView.setText(formattedTimeDate);
			}
		});
		dateTimePickerDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		
		dateTimePickerDialog.setTitle("Choose date and time");		
		dateTimePickerDialog.show();	
	}
	
	public void submitTask(View v) {
		// Check to make sure all options have been selected.
		if (taskEditText.getText().length() == 0 || dateTextView.getText().length() == 0 || 
				reminderSpinner.getSelectedItemPosition() == 0 || proofSpinner.getSelectedItemPosition() == 0 || pointSpinner.getSelectedItemPosition() == 0) {

			Toast notSelectedToast = Toast.makeText(this, "Looks like something still needs to be selected!", Toast.LENGTH_LONG);
			notSelectedToast.show();
		} else {
			// Gather data entered by user in order to create a new task.
			String taskText = taskEditText.getText().toString();

			SimpleDateFormat timeDateFormatter = new SimpleDateFormat("MM/dd/yy h:mm a", Locale.getDefault());
			Calendar timeDate = Calendar.getInstance();
			
			// Check to make sure the date can be properly formatted from the dateTextView
			try {
				timeDate.setTime(timeDateFormatter.parse(dateTextView.getText().toString()));
			} catch (ParseException e) {
				Toast dateErrorToast = Toast.makeText(this, "Oops, something's wrong with the date. Did you set a date to get it done?", 
						Toast.LENGTH_LONG);
				dateErrorToast.show();
			}

			String reminder = reminderSpinner.getSelectedItem().toString();
			String proof = proofSpinner.getSelectedItem().toString();
			
			// Extract just the integer from the pointSpinner selection.
			String[] splitPointText = pointSpinner.getSelectedItem().toString().split(" ");
			int points = Integer.parseInt(splitPointText[0]);
		 
			// Creates taskId based on the scheduled time. Later tasks will always be a larger number.
			String dateAsNum = "" + timeDate.get(Calendar.YEAR) + "" + timeDate.get(Calendar.MONTH) + "" + timeDate.get(Calendar.DATE)
								+ "" + timeDate.get(Calendar.HOUR) + "" + timeDate.get(Calendar.MINUTE);
			long taskId  = Long.parseLong(dateAsNum);
						
			Task newTask = new Task(taskText, timeDate, reminder, proof, points, taskId);
			
			// Sends task info to taskManager to be updated in database. 
			// "addTask" returns true if successfully added, else false.
			Boolean taskAdded = taskManager.addTask(newTask, getBaseContext());
				
			Intent addTaskResult = new Intent();
			addTaskResult.putExtra("taskAdded", taskAdded);
			if (taskAdded) {
				setResult(Activity.RESULT_OK);
			} else {
				setResult(Activity.RESULT_CANCELED);
			}
			finish();
		}
	}
}
