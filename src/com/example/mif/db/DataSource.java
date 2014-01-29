package com.example.mif.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DataSource {

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
      MySQLiteHelper.COLUMN_WORD, MySQLiteHelper.COLUMN_CAT };
  private String[] allCatColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_CAT };
  /*private String[] allScoreColumns = { MySQLiteHelper.COLUMN_ID,
	      MySQLiteHelper.COLUMN_WORD, MySQLiteHelper.COLUMN_SCORE,
	      MySQLiteHelper.COLUMN_DATE};*/

  public DataSource(Context context) {
    dbHelper = new MySQLiteHelper(context);
    //dbHelper.onUpgrade(db, oldVersion, newVersion)
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public void addWord(String word,String category) {
	  if (!getAllCategories().contains(category)){
		  Log.v(category,"add category");
		  addCategory(category);
	  }
    ContentValues values = new ContentValues();
       
    values.put(MySQLiteHelper.COLUMN_WORD, word);
    values.put(MySQLiteHelper.COLUMN_CAT, category);
    
    Log.v("Values",values.toString());
    Log.v("Database",database.toString());
    database.insert(MySQLiteHelper.TABLE_WORDS, null,values);
  }
  public void addCategory(String category) {
	    ContentValues values = new ContentValues();
	    
	    values.put(MySQLiteHelper.COLUMN_CAT, category);
	    
	    Log.v("Values",values.toString());
	    Log.v("Database",database.toString());
	    database.insert(MySQLiteHelper.TABLE_CAT, null,values);
  }
  
  /*public void addHighScore(String word,int score,boolean evil){
	  Date date = (new java.sql.Date(System.currentTimeMillis()));
	  ContentValues values = new ContentValues(); 
	  values.put(MySQLiteHelper.COLUMN_WORD, word);
	  values.put(MySQLiteHelper.COLUMN_SCORE, score);
	  values.put(MySQLiteHelper.COLUMN_DATE, date.toString());
	  values.put(MySQLiteHelper.COLUMN_EVIL, Boolean.toString(evil));
	  database.insert(MySQLiteHelper.TABLE_HS , null, values); 
  }

  public void deleteWord(Word word) {
    long id = word.getId();
    System.out.println("Comment deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_DICT, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }*/
  public List<String> getAllCategories() {
    List<String> words = new ArrayList<String>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_CAT,
        allCatColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      String word = cursor.getString(1);//cursorToWord(cursor);
      words.add(word);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return words;
  }
  
  public List<String> getAllWordsOf(String category) {
	    List<String> words = new ArrayList<String>();

	    String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_WORDS + " WHERE " + MySQLiteHelper.COLUMN_CAT + " = \"" + (category) + "\"";
	    Cursor cursor = database.rawQuery(selectQuery,null);//query(MySQLiteHelper.TABLE_WORDS,allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      String word = cursor.getString(1);//cursorToWord(cursor);
	      words.add(word);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return words;
	  }
  
  public List<String> getAllWords() {
    List<String> words = new ArrayList<String>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_WORDS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      String word = cursor.getString(1);//cursorToWord(cursor);
      words.add(word);
      cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return words;
  }
  public void deleteWords(List<String>words, String category){


      if(words.size() != 0){
          String deleteQuery = "DELETE FROM " + MySQLiteHelper.TABLE_WORDS;
          deleteQuery += " WHERE " + MySQLiteHelper.COLUMN_CAT + " = '" + category 
        		  + "' AND " + MySQLiteHelper.COLUMN_WORD +" IN ('";
          for(int i=0; i < words.size(); i++) {
              if(i > 0) {
                  deleteQuery += ",'";
              }
              deleteQuery += words.get(i) + "'";
          }
          deleteQuery += ")";
          Log.v("delete query",deleteQuery);
          database.execSQL(deleteQuery);
      }
      if(getAllWordsOf(category).size()==0) deleteCategory(category);

  }
  public void deleteCategory(String category){

      String query1 = "DELETE FROM " + MySQLiteHelper.TABLE_CAT + " WHERE " + MySQLiteHelper.COLUMN_CAT + " = \"" + (category) + "\"";
      String query2 = "DELETE FROM " + MySQLiteHelper.TABLE_WORDS + " WHERE " + MySQLiteHelper.COLUMN_CAT + " = \"" + (category) + "\"";

      database.execSQL(query1);
      database.execSQL(query2);
  }
  
  /*
  
  public List<Score> getAllScores() {
	    List<Score> scores = new ArrayList<Score>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_HS,
	        allScoreColumns, null, null, null, null, "score");

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Score score = cursorToScore(cursor);
	      scores.add(score);
	      cursor.moveToNext();
	    }
	    // make sure to close the cursor
	    cursor.close();
	    return scores;
	  }
  
  public int getMaxLength() {
	    final SQLiteStatement stmt = database.compileStatement("SELECT MAX(length) FROM " +MySQLiteHelper.TABLE_DICT);

	    return (int) stmt.simpleQueryForLong();
  }
  
 

  private String cursorToWord(Cursor cursor) {
    Word word = new Word();
    word.setId(cursor.getLong(0));
    word.setWord(cursor.getString(1));
    word.setLength(cursor.getInt(2));
    return word;
  }
  
  private Score cursorToScore(Cursor cursor) {
	    Score score = new Score();
	    score.setId(cursor.getLong(0));
	    score.setWord(cursor.getString(1));
	    score.setScore(cursor.getInt(2));	    
	    score.setDate(cursor.getString(3));
	    score.setEvil(Boolean.parseBoolean(cursor.getString(4)));
	    return score;
	  }
 */
  
}