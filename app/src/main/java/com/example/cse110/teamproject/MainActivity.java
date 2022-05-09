package com.example.cse110.teamproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView dropdown;
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
        dropdown = findViewById(R.id.search_bar);

        adapter = new ExhibitsListAdapter();

        exhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .exhibitListItemDao();
        //List<ExhibitNodeItem> exhibitNodeItems = exhibitListItemDao.getAllExhibits();

        userExhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .userExhibitListItemDao();
        userExhibitListItemDao.deleteUserExhibitItems();


        List<String> exhibits = exhibitListItemDao.getAllExhibits().stream().map(e -> e.name).collect(Collectors.toList());

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, exhibits
                .toArray(new String[exhibits.size()]));


        dropdown.setAdapter(arrayAdapter);
        // number of letters needed in order for auto-completion to activate
        dropdown.setThreshold(1);

        dropdown.setOnKeyListener((view, i, keyEvent) -> {
            // if ENTER is pressed on keyboard, show search results page and reset search entry
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                String input = dropdown.getText().toString();
                // reset search entry
                dropdown.setText(null);
                intentSearchResults.putExtra("key", input);
                // need to replace Log statement with Intent for search results page
                startActivity(intentSearchResults);
            }
            return false;
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
                dropdown.setText(null);
            }
        });

        startUserListRecycler();
        setUpUserListSizeListener();


    }

    public void onSearchIconClicked(View view) {
        Intent intentSearchResults = new Intent(this, SearchResultsActivity.class);
//        AutoCompleteTextView dropdown = findViewById(R.id.search_bar);
        String input = dropdown.getText().toString();
        intentSearchResults.putExtra("key", input);
        startActivity(intentSearchResults);
        dropdown.setText(null);
    }

    private void startUserListRecycler() {
        userListRecycler = findViewById(R.id.user_list);
        userListRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter.setHasStableIds(true);

        userListRecycler.setAdapter(adapter);

        adapter.setExhibitListItems(userExhibitListItemDao.getAllUserExhibits());
    }

    private void setUpUserListSizeListener() {
        UserListHeaderViewModel viewModel = new ViewModelProvider(this)
                .get(UserListHeaderViewModel.class);
        viewModel.getListSize().observe(this, this::onUserListSizeChange);
    }

    private void onUserListSizeChange(int size) {
        TextView userListHeader = findViewById(R.id.user_list_header);
        userListHeader.setText(String.format("My List (%d)", size));

        Button planButton = findViewById(R.id.plan_btn);
        planButton.setEnabled(size > 0);
    }

    public void addExhibitToUserList(String exhibitName) {
        UserExhibitListItem newItem = new UserExhibitListItem(exhibitListItemDao.getExhibitByName(exhibitName).node_id);
        userExhibitListItemDao.insert(newItem);
    }


    public void onPlanClicked(View view) {
        // only open plan if at least one use exhibit
            Intent intent = new Intent(this, PlanActivity.class);
            startActivity(intent);
//        }
    }
}