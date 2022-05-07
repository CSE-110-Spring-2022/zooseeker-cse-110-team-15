package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ExhibitsDirectionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits_directions);

        //UserExhibitListItem->PathFinder
        //
    }

    public void onPreviousIconClicked(View view) {
        Button button = (Button) findViewById(R.id.button_send);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

    }

    public void onNextIconClicked(View view) {

    }

}