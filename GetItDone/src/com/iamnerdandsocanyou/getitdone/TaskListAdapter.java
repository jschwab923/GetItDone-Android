package com.iamnerdandsocanyou.getitdone;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TaskListAdapter extends ArrayAdapter<Task> {
        
        private ArrayList<Task> taskList;
        
        public TaskListAdapter(Context context, int viewResourceId, ArrayList<Task> taskList) {
                super(context, viewResourceId, taskList);
                this.taskList = taskList;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                
                if (v == null) {
                	LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.tasklist_textview, null);
                }
                
                Typeface customFontLight = Typeface.createFromAsset(v.getContext().getAssets(), v.getContext().getString(R.string.custom_font_light));
                
                Task currentTask = taskList.get(position);
                
                TextView taskText = (TextView) v.findViewById(R.id.taskListTextView);
                taskText.setTypeface(customFontLight);
                taskText.setText(currentTask.taskText);
                
                return v;
        }
}