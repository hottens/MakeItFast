 package com.example.mif;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }   
    public void play(View v){
    	Intent intent = new Intent(this, AddWordActivity.class);
        startActivity(intent);
    }
    public void rules(View v){
    	Toast.makeText(this, "rules", Toast.LENGTH_SHORT).show();
    }
}
