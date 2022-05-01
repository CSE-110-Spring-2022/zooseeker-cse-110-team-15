package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.util.Log;
import android.view.View.OnKeyListener;

import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    // array adapter for dropdown
    ArrayAdapter<String> arrayAdapter;
    ExhibitsListAdapter adapter;
    public RecyclerView userListRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Intent intentSearchResults = new Intent(this, SearchResultsActivity.class);
        setContentView(R.layout.activity_main);

        // auto complete text for search & auto completion dropdown
        AutoCompleteTextView dropdown = findViewById(R.id.search_bar);

        ExhibitListItemDao exhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .exhibitListItemDao();
        List<ExhibitNodeItem> exhibitNodeItems = exhibitListItemDao.getAllExhibits();

        List<String> exhibits = ExhibitNodeItem.loadJSON(this, "sample_node_info.json")
                .stream().map(item -> item.name).collect(Collectors.toList());

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, exhibits
                .toArray(new String[exhibits.size()]));


        dropdown.setAdapter(arrayAdapter);
        // number of letters needed in order for auto-completion to activate
        dropdown.setThreshold(1);

        dropdown.setOnKeyListener(new OnKeyListener()  {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // if ENTER is pressed on keyboard, show search results page and reset search entry
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    // need to replace Log statement with Intent for search results page
                    startActivity(intentSearchResults);
                    // reset search entry
                    dropdown.setText("");
                }
                return false;
            }
        });
        startUserListRecycler();
    }
    private void startUserListRecycler() {
        userListRecycler = findViewById(R.id.user_list);
        userListRecycler.setLayoutManager(new LinearLayoutManager(this));
        ExhibitsListAdapter adapter = new ExhibitsListAdapter();
        adapter.setHasStableIds(true);

        userListRecycler.setAdapter(adapter);
        //TODO: add actual selected exhibits here
        adapter.setExhibitListItems(ExhibitNodeItem.loadJSON(this, "demo_exhibits.json"));
    }

    public void onSearchIconClicked(View view) {
        Intent intentSearchResults = new Intent(this, SearchResultsActivity.class);
        AutoCompleteTextView dropdown = findViewById(R.id.search_bar);
        startActivity(intentSearchResults);
        dropdown.setText("");
    }
}