package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Source: https://stackoverflow.com/questions/24885223/why-doesnt-recyclerview-have-onitemclicklistener
 */
public class SearchResultsActivity extends AppCompatActivity {
    public RecyclerView recyclerView;

    UserExhibitListItemDao userExhibitListItemDao;
    ExhibitListItemDao exhibitListItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        // Get DAOs (singleton)
        exhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .exhibitListItemDao();
        List<ExhibitNodeItem> exhibitNodeItems = exhibitListItemDao.getAllExhibits();

        userExhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .userExhibitListItemDao();

        // Set up adapter for list of exhibits
        ExhibitsListAdapter adapter = new ExhibitsListAdapter();
        adapter.setHasStableIds(true);
        adapter.setExhibitListItems(exhibitNodeItems);

        recyclerView = findViewById(R.id.exhibit_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        ExhibitNodeItem exhibitNodeItem = adapter.getItem(position);
                        Log.d("value of text: ", exhibitNodeItem.name);
                        addExhibitToUserList(exhibitNodeItem.name);
                    }
                });

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
        finish();
    }

    public void addExhibitToUserList(String exhibitName) {
        UserExhibitListItem newItem = new UserExhibitListItem(exhibitListItemDao.getExhibitByName(exhibitName).node_id);
        userExhibitListItemDao.insert(newItem);
    }
}