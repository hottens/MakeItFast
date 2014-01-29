package com.example.mif;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mif.model.Game;

public class GameActivity extends Activity {
	private Game game;
	ListView listView;
	TextView team;
	TextView time;
	View colorBar;
	Button next;
	Button endButton;
	AlertDialog infoDialog;
	private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    boolean scoreboard = false;
    boolean end = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setNegativeButton(getString(R.string.cancel), new CancelOnClickListener());
        builder.setNeutralButton(getString(R.string.ok),new okOnClickListener());
        infoDialog = builder.create();
		
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();

        ArrayList<String> selectedCategories = b.getStringArrayList(getString(R.string.selectedCategories));
        int nSec = Integer.parseInt(b.getString(getString(R.string.seconds)));
        int nWords = Integer.parseInt(b.getString(getString(R.string.words)));
        int nTeams = Integer.parseInt(b.getString(getString(R.string.teams)));
	
		game = new Game(selectedCategories,nSec,nWords,nTeams,this);
		this.colorBar = (View) findViewById(R.id.teamtime);
		this.listView = (ListView) findViewById(R.id.word_list);
		this.team = (TextView) findViewById(R.id.teamTextView);
		this.next = (Button) findViewById(R.id.next1);
		this.time = (TextView) findViewById(R.id.timer);
		this.endButton = (Button) findViewById(R.id.end);
		
		countDownTimer = new MyCountDownTimer(game.startTime, game.interval);
		
		
		loadWords();
				
	}
	public void  loadWords() {
		if(game.outOfWords()){
			//Game over
			next(colorBar.getRootView());
			end = true;
			return;
		}
		
		this.team.setText(getString(R.string.team) + " " + game.getTeam());
		this.colorBar.setBackgroundColor(game.getColor());
		
        next.setVisibility(View.GONE);
        endButton.setVisibility(View.GONE);
        

        // Set the color
        //root.setBackgroundColor(colors[team]);
        timer();
        
    	ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice, game.getNWords());
        this.listView.setAdapter(arrayAdapter);
        scoreboard = true;
    }
	public void next(View view){
		
        if(scoreboard){
        	addScore();
        	ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, game.getScores());
            this.listView.setAdapter(arrayAdapter);
            scoreboard = false;
        } else {
        	scoreboard = true;
            game.nextTeam();
            loadWords();
        }
        if(end){ 
        	next.setVisibility(View.INVISIBLE);
        	endButton.setVisibility(View.INVISIBLE);
        	infoDialog.setMessage(game.getBestTeam());
        	infoDialog.show();
        }
        
    }
	
	public void end(View v){
		end = true;
		infoDialog.setMessage(getString(R.string.cancelMessage));
    	infoDialog.show(); 
	}
	public void addScore(){

        SparseBooleanArray checked = listView.getCheckedItemPositions();
        Log.v("checked size",String.valueOf(checked.size()));
        game.addToScore(checked.size());
    }


	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
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
	
	public void timer() {
        countDownTimer.start();
        timerHasStarted = true;

    }
    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void onFinish() {
            time.setText(getString(R.string.timeIsUp));
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            next.setVisibility(View.VISIBLE);
            endButton.setVisibility(View.VISIBLE);

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v.hasVibrator()) {
                Log.v("Can Vibrate", "YES");
                v.vibrate(5000);
            } else {
                Log.v("Can Vibrate", "NO");
            }
        }


        @Override
        public void onTick(long millisUntilFinished) {
            time.setText("" + millisUntilFinished / 1000);
        }


    }
    private final class CancelOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Log.v("Dialog","cancel");
			end = false;
		    //Toast.makeText(getApplicationContext(), getString(R.string.activityWillContinue),Toast.LENGTH_LONG).show();
		}
    }
    private final class okOnClickListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			Log.v("Dialog","ok");
	    	next(colorBar.getRootView());
		    //Toast.makeText(getApplicationContext(), getString(R.string.activityWillContinue),Toast.LENGTH_LONG).show();
		}
    }


}
