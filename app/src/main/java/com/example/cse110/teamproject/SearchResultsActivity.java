package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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


        Bundle extras = getIntent().getExtras();
        if(extras == null){
            Bundle holder = new Bundle();
            holder.putString("key", "");
            extras = holder;
        }
        String search = extras.getString("key");
        TextView textView = findViewById(R.id.search_results);
        textView.setText("Search results for \"" + search + "\"");

        //populate recyclerview with queried items
        TextView exhibitItem = findViewById(R.id.exhibit_item_text);
        List<ExhibitNodeItem> exhibitNodeItemList = exhibitListItemDao.getExhibits(search);
        adapter.setExhibitListItems(exhibitNodeItemList);

    }


    public void backToSearchClicked(View view) {
        //Log.d("SearchResultsActivity", "yo");
        finish();
    }
}