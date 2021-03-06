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
    final String USER_LIST_TITLE = "My List (%d)";

    ExhibitsListAdapter adapter;
    ArrayAdapter<String> arrayAdapter;
    AutoCompleteTextView dropdown;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;
    UserExhibitListViewModel userExhibitListViewModel;
    public RecyclerView userListRecycler;


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


        userExhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .userExhibitListItemDao();

        Log.d("<database>", exhibitListItemDao.getAllExhibits().toString());

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
                addExhibitToUserList(viewText);
                dropdown.setText(null);
            }
        });

        startUserListRecycler();
        setUpUserListSizeListener();

    }



//    protected void onResume() {
//        super.onResume();
//        userListRecycler.getLayoutManager().onRestoreInstanceState(state);
//    }

    @Override
    public void onDestroy() {
        //state = userListRecycler.getLayoutManager().onSaveInstanceState();
        super.onDestroy();


    }



    public void onSearchIconClicked(View view) {
        Intent intentSearchResults = new Intent(this, SearchResultsActivity.class);
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
        userListHeader.setText(String.format(USER_LIST_TITLE, size));

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

    public void onSettingsButtonClicked(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

//    public void loadResults() {
//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//
//
//
//    }

    //public void saveResults() {

        //want to iterate through

//        for (int i = 0; i < userListRecycler.getAdapter().getItemCount(); i++) {
//            RecyclerView.ViewHolder viewHolder = userListRecycler.getChildViewHolder(userListRecycler.getChildAt(i));
//            editor.putString(Integer.toString(i), viewHolder.toString());
//        }
//        for (int i = 0; i < adapter.getItemCount(); i++) {
//            adapter.onCreateViewHolder(userExhibitListViewModel, i);
//        }

        //editor.apply();
    //}
}