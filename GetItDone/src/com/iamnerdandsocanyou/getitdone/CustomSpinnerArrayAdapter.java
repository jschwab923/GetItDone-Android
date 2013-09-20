package com.iamnerdandsocanyou.getitdone;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerArrayAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private String[] choices;
	
	final Typeface customFontLight;
	final Typeface customFontRegular;
	
	public CustomSpinnerArrayAdapter(Context context, int viewResourceId, String[] choices) {
		super(context, viewResourceId, choices);
		this.context = context;
		this.choices = choices;	
		
		customFontLight = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.custom_font_light));
		customFontRegular = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.custom_font_regular));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.spinner_item, null);
		}
		
		String currentChoice = choices[position];
		
		if (currentChoice != null) {
			TextView spinnerTextView = (TextView)row.findViewById(R.id.spinnerTextView);
			spinnerTextView.setTypeface(customFontLight);
			if (spinnerTextView != null) {
				spinnerTextView.setText(currentChoice);
			}
		}
		
		return row;
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.spinner_item, null);
		}
		
		String currentChoice = choices[position];
		
		if (currentChoice != null) {
			TextView spinnerTextView = (TextView)row.findViewById(R.id.spinnerTextView);
			spinnerTextView.setTypeface(customFontLight);
			if (spinnerTextView != null) {
				spinnerTextView.setText(currentChoice);
			}
		}
		
		return row;
	}
}

