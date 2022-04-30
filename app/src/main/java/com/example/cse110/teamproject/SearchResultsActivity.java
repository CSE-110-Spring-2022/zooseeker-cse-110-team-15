package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import java.util.List;
import java.util.stream.Collectors;

public class SearchResultsActivity extends AppCompatActivity {
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        ExhibitListItemDao exhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .exhibitListItemDao();
        List<ExhibitNodeItem> exhibitNodeItems = exhibitListItemDao.getAllExhibits();

        ExhibitsListAdapter adapter = new ExhibitsListAdapter();
        adapter.setHasStableIds(true);
        adapter.setExhibitListItems(exhibitNodeItems);

        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        List<String> exhibits = ExhibitNodeItem.loadJSON(this, "demo_exhibits.json").stream().map(item -> item.name).collect(Collectors.toList());



        //adapter.setExhibitListItems(exhibits);


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