package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * Source: https://stackoverflow.com/questions/24885223/why-doesnt-recyclerview-have-onitemclicklistener
 */
public class SearchResultsActivity extends AppCompatActivity {
    final String SEARCH_FORMAT = "Search results for \"%s\"";

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

        userExhibitListItemDao = ExhibitDatabase.getSingleton(this)
                .userExhibitListItemDao();

        // Set up adapter for list of exhibits
        ExhibitsListAdapter adapter = new ExhibitsListAdapter();
        adapter.setHasStableIds(true);

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
        String formatted = String.format(SEARCH_FORMAT, search);
        TextView textView = findViewById(R.id.search_results);
        textView.setText(formatted);

        //populate recyclerview with queried items
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