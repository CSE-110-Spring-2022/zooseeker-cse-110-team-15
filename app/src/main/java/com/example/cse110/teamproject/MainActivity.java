package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
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

        Intent intentSearchResults = new Intent(this, SearchResultsActivity.class);
        setContentView(R.layout.activity_main);

        // auto complete text for search & auto completion dropdown
        AutoCompleteTextView dropdown = (AutoCompleteTextView) findViewById(R.id.search_bar);

        adapter = new ExhibitsListAdapter();

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
                // if ENTER is pressed on keyboard, show search results page and reset search entry
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    String input = dropdown.getText().toString();
                    intentSearchResults.putExtra("key", input);
                    // need to replace Log statement with Intent for search results page
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

        dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {
                String viewText = (String) adapterView.getItemAtPosition(position);
                Log.d("value of text: ", viewText);

                addExhibitToUserList(viewText);
            }
        });

        startUserListRecycler();
        setUpUserListHeader();
    }

    public void onSearchIconClicked(View view) {
        Intent intentSearchResults = new Intent(this, SearchResultsActivity.class);
        AutoCompleteTextView dropdown = findViewById(R.id.search_bar);
        startActivity(intentSearchResults);
        dropdown.setText("");
    }

    private void startUserListRecycler() {
        userListRecycler = findViewById(R.id.user_list);
        userListRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setHasStableIds(true);

        userListRecycler.setAdapter(adapter);

        adapter.setExhibitListItems(userExhibitListItemDao.getAllUserExhibits());
    }

    private void setUpUserListHeader() {
        UserListHeaderViewModel viewModel = new ViewModelProvider(this)
                .get(UserListHeaderViewModel.class);
        TextView userListHeader = findViewById(R.id.user_list_header);
        viewModel.getListSize().observe(this, (size) -> {
            userListHeader.setText(String.format("My List (%d)", size));
        });
    }

    public void addExhibitToUserList(String exhibitName) {
        UserExhibitListItem newItem = new UserExhibitListItem(exhibitListItemDao.getExhibitByName(exhibitName).node_id);

        userExhibitListItemDao.insert(newItem);
    }
}