package com.example.mif;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mif.db.DataSource;

public class AddWordActivity extends Activity {
	private DataSource db;
	AlertDialog alertDialog;
	String cat;
	ListView listview;
	ArrayAdapter<String> arrayAdapter;
	ArrayAdapter<String> adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		listview = (ListView) findViewById(R.id.list_view);
		alertDialog = new AlertDialog.Builder(this).create();
		
		db = new DataSource(this);
		db.open();
		setContentView(R.layout.activity_add_word);
		setupActionBar();
		
		final List<String> categories = db.getAllCategories();
		categories.add("New Category");
		
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
		        categories);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner.setAdapter(adapter);
		cat = spinner.getItemAtPosition(0).toString();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() 
        {
            @SuppressWarnings("deprecation")
			public void onItemSelected(final AdapterView<?> parent, View view, final int position, long i) 
            {
                if (parent.getItemAtPosition(position).toString().matches(getString(R.string.newCategory))){
                	
                		LayoutInflater li = LayoutInflater.from(parent.getContext());
        				View promptsView = li.inflate(R.layout.prompt, null);
         
        				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
        						parent.getContext());
         
        				alertDialogBuilder.setView(promptsView);
         
        				final EditText userInput = (EditText) promptsView
        						.findViewById(R.id.editTextDialogUserInput);
         
        				alertDialogBuilder
        					.setCancelable(false)
        					.setPositiveButton(getString(R.string.ok),
        					  new DialogInterface.OnClickListener() {
        					    public void onClick(DialogInterface dialog,int id) {
        					    	cat = userInput.getText().toString();
	        						categories.set(position, userInput.getText().toString());
	        						categories.add(getString(R.string.newCategory));
	        						adapter.notifyDataSetChanged();
        					    }
        					  })
        					.setNegativeButton(R.string.cancel,
        					  new DialogInterface.OnClickListener() {
        					    public void onClick(DialogInterface dialog,int id) {
        						dialog.cancel();
        					    }
        					  });
         
        				AlertDialog alertDialog = alertDialogBuilder.create();
         
        				alertDialog.show();
         
        			}
                else{ 
                	cat = parent.getItemAtPosition(position).toString();
                }
                
                loadWords();
                
            }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    

                }
            });
            
		loadWords();
            
	}
	public void  loadWords() {
        
        db.open();
        List<String> all = db.getAllWordsOf(cat);
        listview = (ListView) findViewById(R.id.list_view);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, all);
        if (arrayAdapter==null) Log.v("loadwords","arrayAdapter null");
        listview.setAdapter(arrayAdapter);
        

        db.close();

    }
	public void delete(View v){


        db.open();

        SparseBooleanArray checked = listview.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i))
                selectedItems.add(arrayAdapter.getItem(position));
        }

        List<String> output = new ArrayList<String>();

        for (int i = 0; i < selectedItems.size(); i++) {
            output.add(i, selectedItems.get(i));
        }

        db.deleteWords(output,cat);
        db.close();

        loadWords();
        adapter.notifyDataSetChanged();

    }
	public void play(View v){
    	Intent intent = new Intent(this, GameSettingsActivity.class);
        startActivity(intent);
    }
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_word, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void addWord(View view){
		db.open();
		db.addWord(((EditText)findViewById(R.id.edit_message)).getText().toString(), cat);
		db.close();
		loadWords();
		EditText userInput = (EditText) findViewById(R.id.edit_message);
		userInput.setText("");
		
	}

}
