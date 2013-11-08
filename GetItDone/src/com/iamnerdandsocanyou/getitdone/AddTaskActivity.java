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
import android.content.SharedPreferences;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddTaskActivity extends Activity {
	
	private TextView dateTextView;
	private EditText taskEditText;
	private EditText proofQuestionEditText;
	private Spinner reminderSpinner; 
	private Spinner proofSpinner; 
	private Spinner pointSpinner;
	private Spinner recurringSpinner;
	private Spinner categorySpinner;
	
	
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
		
		proofQuestionEditText = (EditText)findViewById(R.id.proofQuestionEditText);
		proofQuestionEditText.setTypeface(customFontLight);
		
		reminderSpinner = (Spinner)findViewById(R.id.reminderSpinner);
		CustomSpinnerArrayAdapter reminderAdapter = new CustomSpinnerArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.reminder_choices));
		reminderSpinner.setAdapter(reminderAdapter);
		
		proofSpinner = (Spinner)findViewById(R.id.proofSpinner);
		CustomSpinnerArrayAdapter proofAdapter = new CustomSpinnerArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.proof_choices));
		proofSpinner.setAdapter(proofAdapter);
		
		pointSpinner = (Spinner)findViewById(R.id.pointSpinner);
		CustomSpinnerArrayAdapter pointAdapter = new CustomSpinnerArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.point_choices));
		pointSpinner.setAdapter(pointAdapter);
		
		// ****NEED TO SOMEHOW CONNECT THIS WITH THE DATE TO GIVE OPTIONS FOR RECURRING ON SELECTED DAY, PROVIDE CUSTOM RECURRING OPTIONS****
		recurringSpinner = (Spinner)findViewById(R.id.recurringSpinner);
		CustomSpinnerArrayAdapter recurringAdapter = new CustomSpinnerArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.recurring_choices));
		recurringSpinner.setAdapter(recurringAdapter);
		
		// ****NEED TO UPDATE SO CATEGORIES ARE EDITABLE****
		categorySpinner = (Spinner)findViewById(R.id.categorySpinner);
		CustomSpinnerArrayAdapter categoryAdapter = new CustomSpinnerArrayAdapter(this, R.layout.spinner_item, getResources().getStringArray(R.array.category_choices));
		categorySpinner.setAdapter(categoryAdapter);

		Bundle extras = getIntent().getExtras();
		String code = extras.getString("code");
		if (!code.equals("addingTask")) {
			// Disable datePicker
			dateTextView.setClickable(false);
			dateTextView.setAlpha(.4f);
			dateTextView.setText("Can't change date");
			
			taskEditText.setText(extras.getString("taskText"));

			String reminder = extras.getString("reminder");
			String proof = extras.getString("proof");
			int points = extras.getInt("points");
			String category = extras.getString("category");
			String recurring = extras.getString("recurring");
			
			if (reminder.equals(getResources().getStringArray(R.array.reminder_choices)[1])) {
				reminderSpinner.setSelection(1);
			} else {
				reminderSpinner.setSelection(2);
			}

			if (proof.equals(getResources().getStringArray(R.array.proof_choices)[1])) {
				proofSpinner.setSelection(1);
			} else if (proof.equals(getResources().getStringArray(R.array.proof_choices)[2])) {
				proofSpinner.setSelection(2);
			} else {
				proofSpinner.setSelection(3);
			}

			switch(points) {
			case 5:
				pointSpinner.setSelection(1);
				break;
			case 10:
				pointSpinner.setSelection(2);
				break;
			case 15:
				pointSpinner.setSelection(3);
				break;
			case 20:
				pointSpinner.setSelection(4);
				break;
			case 25:
				pointSpinner.setSelection(5);
				break;
			case 30:
				pointSpinner.setSelection(6);
				break;
			}
			String[] categoryChoices = getResources().getStringArray(R.array.category_choices);
			if (category.equals(categoryChoices[1])) {
				categorySpinner.setSelection(1);
			} else if (category.equals(categoryChoices[2])) {
				categorySpinner.setSelection(2);
			} else {
				categorySpinner.setSelection(3);
			}
			
			String[] recurringChoices = getResources().getStringArray(R.array.recurring_choices);
			if (recurring.equals(recurringChoices[1])) {
				recurringSpinner.setSelection(1);
			} else if (recurring.equals(recurringChoices[2])) {
				recurringSpinner.setSelection(2);
			} else if (recurring.equals(recurringChoices[3])) {
				recurringSpinner.setSelection(3);
			} else if (recurring.equals(recurringChoices[4])) {
				recurringSpinner.setSelection(4);
			} else if (recurring.equals(recurringChoices[5])) {
				recurringSpinner.setSelection(5);
							
			getItDoneButton.setText("Update task info");
			
		}
		
		
		reminderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// An item was selected. You can retrieve the selected item using
				// parent.getItemAtPosition(pos)
				final int gently = 1;
				final int vigorously = 2;
				
				
				// Set up extra Views that aren't visible until selection is made with
				// spinners
				TextView reminderDescriptionTextView = (TextView)findViewById(R.id.reminderDescriptionTextView);
				reminderDescriptionTextView.setTypeface(customFontLight);
				
				LayoutParams reminderTextViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							
				switch(pos) {
				case(gently):
					// Change width and height of TextView so text can be shown
					reminderDescriptionTextView.setLayoutParams(reminderTextViewParams);
					reminderDescriptionTextView.setText(Html.fromHtml(getString(R.string.reminder_description_gently)));
					break;
				case(vigorously):
					// Change width and height of TextView so text can be shown					
					reminderDescriptionTextView.setLayoutParams(reminderTextViewParams);
					reminderDescriptionTextView.setText(Html.fromHtml(getString(R.string.reminder_description_vigorously)));
					break;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		proofSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// An item was selected. Can retrieve the selected item using
				// parent.getItemAtPosition(pos)
				final int takeUploadPhoto = 1;
				final int describeTask = 2;
				final int answerQuestion = 3;
						
				switch(pos) {
					case(takeUploadPhoto):
						break;
					case(describeTask):
						break;
					case(answerQuestion):
						EditText questionToAnswerEditText = (EditText)findViewById(R.id.proofQuestionEditText);
						questionToAnswerEditText.setTypeface(customFontLight);
						questionToAnswerEditText.setVisibility(EditText.VISIBLE);
						questionToAnswerEditText.setHint(R.string.proof_enter_question);
						break;
					default:
						break;
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});}
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
		
		Bundle taskInfo = getIntent().getExtras();
		
		// Check to make sure all options have been selected.
		if (taskEditText.getText().length() == 0 || dateTextView.getText().length() == 0 || 
				reminderSpinner.getSelectedItemPosition() == 0 || proofSpinner.getSelectedItemPosition() == 0 || pointSpinner.getSelectedItemPosition() == 0 ||
				categorySpinner.getSelectedItemPosition() == 0 || recurringSpinner.getSelectedItemPosition() == 0) {

			Toast notSelectedToast = Toast.makeText(this, "Looks like something still needs to be selected!", Toast.LENGTH_LONG);
			notSelectedToast.show();
		} else {
			// Gather data entered by user in order to create a new task.
			String taskText = taskEditText.getText().toString();
			
			// Check which date to assign and if it is formattable. 
			Calendar timeDate = checkAndChooseDate(taskInfo);
			
			String reminder = reminderSpinner.getSelectedItem().toString();
			String proof = proofSpinner.getSelectedItem().toString();
			
			// Extract just the integer from the pointSpinner selection.
			String[] splitPointText = pointSpinner.getSelectedItem().toString().split(" ");
			int points = Integer.parseInt(splitPointText[0]);
			
			// Extract category selection
			String category = categorySpinner.getSelectedItem().toString();
			
			// Extract the recurring selection
			String recurring = recurringSpinner.getSelectedItem().toString();
		 
			// Creates taskId based on the scheduled time. Later tasks will always be a larger number.
			long taskId;
			if (taskInfo.getString("code").equals("addingTask")) {
				String dateAsNum = "" + timeDate.get(Calendar.YEAR) + "" + timeDate.get(Calendar.MONTH) + "" + timeDate.get(Calendar.DATE)
								+ "" + timeDate.get(Calendar.HOUR) + "" + timeDate.get(Calendar.MINUTE);
				taskId  = Long.parseLong(dateAsNum);
			} else {
				taskId = taskInfo.getLong("taskId");
			}
						
			Task newTask = new Task(taskText, timeDate, reminder, proof, points, category, recurring, taskId);
			
			// Sends task info to taskManager to be updated in database. 
			// "addTask" returns true if successfully added, else false.
			Boolean taskAdded;
			if (taskInfo.getString("code").equals("addingTask")) {
				taskAdded = taskManager.addTask(newTask, getBaseContext());
			} else {
				taskAdded = taskManager.updateTask(newTask, getBaseContext());
			}
				
			Intent addTaskResult = new Intent();
			addTaskResult.putExtra("taskAdded", taskAdded);
			if (taskAdded) {
				setResult(Activity.RESULT_OK);
			} else {
				setResult(Activity.RESULT_CANCELED);
			}
			
			SharedPreferences sharedPrefs = v.getContext().getSharedPreferences(PrefsStrings.PREFS_NAME, 0);
			if (sharedPrefs.getInt(PrefsStrings.TASKS_ADDED, 0) == 1) {
				taskManager.saveTaskList(getBaseContext());
			}
			finish();
		}
		
	}
	
	private Calendar checkAndChooseDate(Bundle taskInfo) {
		SimpleDateFormat timeDateFormatter = new SimpleDateFormat("MM/dd/yy h:mm a", Locale.getDefault());
		Calendar timeDate = Calendar.getInstance();
		
		// Check to make sure the date can be properly formatted from the dateTextView if new task is being added
		if (taskInfo.getString("code").equals("addingTask")) {
			try {
				timeDate.setTime(timeDateFormatter.parse(dateTextView.getText().toString()));
			} catch (ParseException e) {
				Toast dateErrorToast = Toast.makeText(this, "Oops, something's wrong with the date. Did you set a date to get it done?", 
						Toast.LENGTH_LONG);
				dateErrorToast.show();
			}
		}
		return timeDate;
	}
}
