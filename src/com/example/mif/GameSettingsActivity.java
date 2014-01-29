package com.example.mif;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.mif.db.DataSource;

public class GameSettingsActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    public final static String EXTRA_MESSAGE = "com.example.makeitfast.MESSAGE";
    private static final int DIALOG_ALERT = 10;
    List<String> categories ;
    DataSource entry;
    ListView list;
    ArrayAdapter<String> arrayAdapter;
    SeekBar nTeams;
    TextView tvTeams;

    SeekBar nSec;
    TextView tvSec;

    SeekBar nWords;
    TextView tvWords;
    
    AlertDialog infoDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);
        list = (ListView) findViewById(R.id.list);
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNeutralButton(getString(R.string.ok), new CancelOnClickListener());
        infoDialog = builder.create();

        entry = new DataSource(this);
        entry.open();
        categories = entry.getAllCategories();

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, categories);
        list.setAdapter(arrayAdapter);

        tvTeams = (TextView) findViewById(R.id.nTeams);
        nTeams = (SeekBar) findViewById(R.id.teams);
        nTeams.setOnSeekBarChangeListener(this);

        tvSec = (TextView) findViewById(R.id.nSec);
        nSec = (SeekBar) findViewById(R.id.seconds);
        nSec.setOnSeekBarChangeListener(this);
        
        tvWords = (TextView) findViewById(R.id.nWords);
        nWords = (SeekBar) findViewById(R.id.words);
        nWords.setOnSeekBarChangeListener(this);

    }

    public void newGame(View v) {

    	Intent intent = new Intent(this, GameActivity.class);
    	
        SparseBooleanArray checked = list.getCheckedItemPositions();
        ArrayList<String> selectedItems = new ArrayList<String>();

        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i))
                selectedItems.add(arrayAdapter.getItem(position));
        }

        ArrayList<String> output = new ArrayList<String>();

        for (int i = 0; i < selectedItems.size(); i++) {
            output.add(selectedItems.get(i));
            }
        if(selectedItems.size() == 0){
        	infoDialog.setMessage(getString(R.string.categoryAlertMessage,1));
        	infoDialog.show();
        	//showDialog(DIALOG_ALERT);
        }

        // Create a bundle object
        Bundle cat = new Bundle();
        cat.putStringArrayList(getString(R.string.selectedCategories), output);
        
        Bundle teams = new Bundle();
        teams.putString(getString(R.string.teams),String.valueOf(nTeams.getProgress()+2));
        Bundle sec = new Bundle();
        sec.putString(getString(R.string.seconds), String.valueOf(nSec.getProgress()+10));
        Bundle words = new Bundle();
        words.putString(getString(R.string.words),String.valueOf(nWords.getProgress()+2));
        
        intent.putExtras(words);
        intent.putExtras(sec);
        intent.putExtras(cat);
        intent.putExtras(teams);
        //Log.v("Sending", message);
        if (selectedItems.size()>0 ) startActivity(intent);
        else 
        	Toast.makeText(this,"Enter more than one team", Toast.LENGTH_LONG).show();

    }


    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            //Toast.makeText(getApplicationContext(), getString(R.string.activityWillContinue),Toast.LENGTH_LONG).show();
        }
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int prog = 0;
		switch(seekBar.getId()){
			case(R.id.teams):
				prog = 2+(seekBar.getProgress());
				tvTeams.setText(String.valueOf(prog));
				break;
			case(R.id.seconds):
				prog = 10+(seekBar.getProgress());
				tvSec.setText(String.valueOf(prog));
				break;
			case(R.id.words):
				prog = 2+(seekBar.getProgress());
				tvWords.setText(String.valueOf(prog));
				break;
			default:
				break;
		}		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}



}
