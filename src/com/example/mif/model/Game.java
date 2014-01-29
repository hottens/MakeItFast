package com.example.mif.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.example.mif.R;
import com.example.mif.db.DataSource;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;

public class Game {
	private DataSource data;
	private CountDownTimer countDownTimer;
    private boolean timerHasStarted = false;
    public long startTime;
    public final long interval = 1 * 1000;
    int team,teams;
    int scores[] = new int[]{0,0,0,0,0,0};
    int colors[] = new int[]{Color.BLUE,Color.YELLOW,Color.RED,Color.BLACK,Color.GRAY};;
    int nWords;
    Context c;
    ArrayList<String> selectedCategories;
    
    ArrayList<String> all;
    ArrayList<String> scoreboard;
    
    public Game(ArrayList<String> selectedCategories, int nSec, int nWords, int nTeams,Context c){
    	this.selectedCategories = selectedCategories;
    	this.startTime = nSec*1000;
    	this.nWords = nWords;
    	this.teams = nTeams;
    	this.c = c;
    	team = 0;
    	data = new DataSource(this.c);
    	data.open();
    	all = new ArrayList<String>();
    	for (int i = 0; i<this.selectedCategories.size();i++){
    		all.addAll(data.getAllWordsOf(this.selectedCategories.get(i)));    	
    	}
    	data.close();
    }
    public void nextTeam(){
    	this.team++;
    	if (this.team==this.teams) this.team=0;
    }
    public int getTeam(){
    	return this.team+1;
    }
    public int getColor(){
    	return this.colors[this.team];
    }
    public ArrayList<String>getNWords(){
    	Random rand = new Random();
    	ArrayList<String> words = new ArrayList<String>();
    	for(int i = 0; i< this.nWords;i++){
    		int index = rand.nextInt(this.all.size());
    		words.add(all.get(index));
    		all.remove(index);
    	}
    	return words;
    }
    public ArrayList<String> getScores(){
    	ArrayList<String> scoreList = new ArrayList<String>();
    	for(int i = 0; i<this.teams;i++){
    		scoreList.add("Team " + (i+1) + "\t" + this.scores[i]);
    	}
    	return scoreList;
    }
    
    public void addToScore(int i){
    	this.scores[getTeam()-1]+=i;
    }
	public boolean outOfWords() {
		if (all.size()<nWords) return true;
		return false;
	}
	public String getBestTeam() {
		int max = 0,index=0;
		boolean draw = false;
		for (int i=0;i<scores.length;i++){
			if (scores[i]>max) {
				draw = false;
				max = scores[i];
				index = i;
			} else if(scores[i]==max) draw = true;
		}
		if (draw) return "Draw";
		return "Team " + (index+1) + " wins!";
	}

}
