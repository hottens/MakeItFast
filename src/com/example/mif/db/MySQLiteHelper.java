package com.example.mif.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.mif.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	Context c;
	
	
  private static final String DATABASE_NAME = "mif.db";
  private static final int DATABASE_VERSION = 43 ;

  public static final String TABLE_WORDS = "words";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_WORD = "word";
  public static final String COLUMN_CAT = "category";
  
  public static final String TABLE_CAT = "categories";

  

  // Database creation sql statement
  private static final String DICT_CREATE = "create table "
      + TABLE_WORDS 
      + "(" 
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_WORD + " TEXT not null, "
      + COLUMN_CAT + " TEXT NOT NULL "
      + ");";
  private static final String CAT_CREATE = "create table "
	      + TABLE_CAT 
	      + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, "
	      + COLUMN_CAT + " TEXT NOT NULL UNIQUE"
	      + ");";
  
  public static final String TABLE_HS = "highscores";
  public static final String COLUMN_SCORE = "score";
  public static final String COLUMN_DATE = "date";
  
  private static final String HS_CREATE = "create table "
	      + TABLE_HS 
	      + "(" 
	      + COLUMN_ID + " integer primary key autoincrement, " 
	      + COLUMN_WORD + " TEXT not null, "
	      + COLUMN_SCORE+ " INT NOT NULL, "
	      + COLUMN_DATE+ " DATE NOT NULL "
	      + ");";

  public MySQLiteHelper(Context context) {
	  super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  this.c = context;
	  Log.v("MySQLiteHelper","init");
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
	  Log.v("onCreave SQLite helper","done");
	  //Log.v("MySQLiteHelper","oncreate");
    database.execSQL(DICT_CREATE);
    database.execSQL(CAT_CREATE);
    try {
		parseXMLtoDB(database);
	} catch (XmlPullParserException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    //Log.v("Create HighScores",HS_CREATE);
    //database.execSQL(HS_CREATE);
  }
  
  public void parseXMLtoDB(SQLiteDatabase database) throws XmlPullParserException, IOException {
	 Log.v("parse","start parsing");
	 //data = new DataSource(c);
	 //data.open();
	 XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	 factory.setNamespaceAware(true);
	 XmlPullParser xpp = factory.newPullParser();
	 InputStream raw = this.c.getResources().openRawResource(R.raw.mif);
	 xpp.setInput(new InputStreamReader(raw));
	 int eventType = xpp.getEventType();
	 while (eventType != XmlPullParser.END_DOCUMENT) {
		 if(eventType == XmlPullParser.END_TAG) {
			 if(xpp.getName().equals("words")) break;
		 }
		 String word="",category="";
		 int countWord=0;
		 while(countWord<2 && eventType != XmlPullParser.END_DOCUMENT){
			 if(eventType == XmlPullParser.END_TAG) {
				 if(xpp.getName().equals("word")) break;
			 }
			 if(eventType == XmlPullParser.START_TAG) {
				 Log.v(xpp.getName(),"word");
				 if(xpp.getName().equals("word")) countWord++;
				 //Log.v("parse",xpp.getName());
				 
				 if (xpp.getName().equals("category")){
					 if(xpp.next()== XmlPullParser.TEXT) {
						 if (!(xpp.getText().length()==0)){
							 category = xpp.getText();
						 }						 	
					 }
				 } else if (xpp.getName().equals("name")){
					 if(xpp.next()== XmlPullParser.TEXT) {
						 Log.v(xpp.getText(),"parse word");
						 if (!(xpp.getText().length()==0))
							 word = xpp.getText(); 
						 Log.v(word,"parse word 2");
					 }
				 }
			 }
			 eventType = xpp.next();
		 }
		 ContentValues values = new ContentValues(); 
		 //Log.v(word + " " + category,"add word");
		 if(!(word.length()==0)&&!(category.length()==0)){ 
			 Log.v(word + " " + category,"add word");
			 values.put(MySQLiteHelper.COLUMN_CAT, category);
			 database.execSQL("INSERT OR IGNORE INTO " + TABLE_CAT + " (" + COLUMN_CAT + ") values ('" + category +"')");//)insert(TABLE_CAT , null, values); 
			 values.put(MySQLiteHelper.COLUMN_WORD, word);
			 database.insert(TABLE_WORDS , null, values); 
		 }
		 eventType = xpp.next();
		 
		 //eventType = xpp.next(); //
	  }
  
	 //data.close();
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT);
    onCreate(db);
  }

} 