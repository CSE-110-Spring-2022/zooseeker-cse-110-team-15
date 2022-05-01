package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.util.Log;
import android.view.View.OnKeyListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    // Temporary sample list to test Search dropdown
    String[] myList = new String[] {"Polar Bear", "Grizzly Bear", "Apple Pie",
                                    "Godzilla", "Paul", "Michael","Lucy", "Samuel", "Larry", "Prem"};



    // array adapter for dropdown
    ArrayAdapter<String> arrayAdapter;
    ExhibitsListAdapter adapter;
    public RecyclerView userListRecycler;

    UserExhibitListItemDao userExhibitListItemDao;
    ExhibitListItemDao exhibitListItemDao;

    UserExhibitListViewModel userExhibitListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        Intent intentSearchResults = new Intent(this, SearchResultsActivity.class);
        setContentView(R.layout.activity_main);

        // auto complete text for search & auto completion dropdown
        AutoCompleteTextView dropdown = (AutoCompleteTextView) findViewById(R.id.search_bar);

        adapter = new ExhibitsListAdapter();
//        adapter.setHasStableIds(true);
//        adapter.setExhibitListItems(ExhibitNodeItem.loadJSON(this, "sample_node_info.json"));

        exhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .exhibitListItemDao();
        List<ExhibitNodeItem> exhibitNodeItems = exhibitListItemDao.getAllExhibits();

        userExhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .userExhibitListItemDao();
        userExhibitListItemDao.deleteUserExhibitItems();
        List<String> userExhibitNodeItems = userExhibitListItemDao.getAllUserExhibitNames();


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
                // if ENTER is pressed on keyboard, show search results page
                // and reset search entry
                if (i == KeyEvent.KEYCODE_ENTER) {
                    // need to replace Log statement with Intent for search results page
                    Log.d("Enter", "Enter working");
                    startActivity(intentSearchResults);
                    // reset search entry
                    dropdown.setText("");
                }
                return false;
            }
        });

        userExhibitListViewModel = new ViewModelProvider(this)
                .get(UserExhibitListViewModel.class);
        userExhibitListViewModel.getExhibitListItems().observe(this, adapter::setExhibitListItems);

        startUserListRecycler();

//        dropdown.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
//
//
////            @Override
////            public void onClick(View view) {
////                adapter.notifyDataSetChanged();
////            }
//        });

        dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                String viewText = (String) adapterView.getItemAtPosition(position);
                Log.d("value of text: ", viewText);

                addExhibitToUserList(viewText);
            }
        });
    }

    private void startUserListRecycler() {
        userListRecycler = findViewById(R.id.user_list);
        userListRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setHasStableIds(true);

        userListRecycler.setAdapter(adapter);
        //TODO: add actual selected exhibits here

        adapter.setExhibitListItems(userExhibitListItemDao.getAllUserExhibits());

//        adapter.setExhibitListItems(ExhibitNodeItem.loadJSON(this, "demo_exhibits.json"));
//        adapter.setExhibitListItems(userExhibitListItemDao.getAllUserExhibitNames());
//        exhibitListItemDao.getAllExhibits().stream().map(item -> item.name).collect(Collectors.toList());
//        Log.d("lskflkj", getUserSelectedExhibits().toString());
    }

    public void addExhibitToUserList(String exhibitName) {
        UserExhibitListItem newItem = new UserExhibitListItem(exhibitListItemDao.getExhibitByName(exhibitName).node_id);
        //UserExhibitListItem newItem = new UserExhibitListItem("sus_place");

        userExhibitListItemDao.insert(newItem);
    }

    public List<String> getUserSelectedExhibits() {
        return userExhibitListItemDao.getAllUserExhibitNames();
    }

}