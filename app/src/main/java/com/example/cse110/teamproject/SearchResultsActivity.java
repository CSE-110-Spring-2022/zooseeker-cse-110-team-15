package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ExhibitsListAdapter adapter = new ExhibitsListAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setExhibitListItems(ExhibitListItem.loadJSON(this, "demo_exhibits.json"));



//        //NEED USER INPUT FROM SEARCH ACTVITY
//        String search = "bear";
//
//        TextView textView = findViewById(R.id.search_results);
//        textView.setText("Search results for \"" + search + "\"");
//
//        List<ExhibitListItem> exhibits = ExhibitListItem.loadJSON(this, "demo_exhibits.json");
//        Log.d("SearchResultsActivity", exhibits.toString());

    }


    public void backToSearchClicked(View view) {
        //Log.d("SearchResultsActivity", "yo");
        finish();
    }
}