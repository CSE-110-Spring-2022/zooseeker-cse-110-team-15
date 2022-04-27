package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.util.Log;
import android.view.View.OnKeyListener;

public class MainActivity extends AppCompatActivity {

    // Temporary sample list to test Search dropdown
    String[] myList = new String[] {"Polar Bear", "Grizzly Bear", "Apple Pie",
                                    "Godzilla", "Paul", "Michael","Lucy", "Samuel", "Larry", "Prem"};

    // array adapter for dropdown
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // auto complete text for search & auto completion dropdown
        AutoCompleteTextView dropdown = (AutoCompleteTextView) findViewById(R.id.search_bar);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, myList);
        dropdown.setAdapter(arrayAdapter);

        // number of letters needed in order for auto-completion to activate
        dropdown.setThreshold(1);

        dropdown.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // if ENTER is pressed on keyboard, show search results page
                // and reset search entry
                if (i == KeyEvent.KEYCODE_ENTER) {
                    // need to replace Log statement with Intent for search results page
                    Log.d("Enter", "Enter working");
                    // reset search entry
                    dropdown.setText("");
                }
                return false;
            }
        });
    }
}