package com.example.cse110.teamproject;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.Rule;

import java.util.List;

@RunWith(AndroidJUnit4.class)
    public class SearchActivityTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;
    PathItemDao pathItemDao;

    @Before
    public void setUp()  {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries().build();

        List<ExhibitNodeItem> nodes = ExhibitNodeItem
                .loadJSON(context, "zoo_node_info.json");
        System.out.println("nodes from json: " + nodes.toString());
        exhibitListItemDao = testDb.exhibitListItemDao();
        exhibitListItemDao.insertAll(nodes);
        userExhibitListItemDao = testDb.userExhibitListItemDao();
        pathItemDao = testDb.pathItemDao();

        ExhibitDatabase.injectTestDatabase(testDb);
    }

    @After
    public void tearDown() {
        ExhibitDatabase.resetSingleton();
        testDb.close();
    }

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void SearchBarDisplayed() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, (activity.findViewById(R.id.search_bar)).getVisibility());
            assertEquals("Search Animal Exhibits", ((AutoCompleteTextView) activity.findViewById(R.id.search_bar)).getHint());
        });
        ExhibitDatabase.resetSingleton();
    }

    @Test
    public void SearchTitleDisplayed() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals("Search", ((TextView) activity.findViewById(R.id.search_page)).getText());
        });
        ExhibitDatabase.resetSingleton();
    }

    @Test
    public void SearchIconDisplayed() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, ((ImageView) activity.findViewById(R.id.search_icon)).getVisibility());
        });
        ExhibitDatabase.resetSingleton();
    }

}
