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
  

  public DataSource(Context context) {
    dbHelper = new MySQLiteHelper(context);
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
  
  public List<String> getAllCategories() {
    List<String> words = new ArrayList<String>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_CAT,
        allCatColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      String word = cursor.getString(1);
      words.add(word);
      cursor.moveToNext();
    }
    cursor.close();
    return words;
  }
  
  public List<String> getAllWordsOf(String category) {
	    List<String> words = new ArrayList<String>();

	    String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_WORDS + " WHERE " + MySQLiteHelper.COLUMN_CAT + " = \"" + (category) + "\"";
	    Cursor cursor = database.rawQuery(selectQuery,null);//query(MySQLiteHelper.TABLE_WORDS,allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      String word = cursor.getString(1);
	      words.add(word);
	      cursor.moveToNext();
	    }
	    cursor.close();
	    return words;
	  }
  
  public List<String> getAllWords() {
    List<String> words = new ArrayList<String>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_WORDS,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      String word = cursor.getString(1);
      words.add(word);
      cursor.moveToNext();
    }
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
  
  
}