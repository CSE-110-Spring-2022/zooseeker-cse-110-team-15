package com.example.cse110.teamproject;


import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


@RunWith(AndroidJUnit4.class)
public class ExhibitsDirectionsActivityTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;
    PathItemDao pathItemDao;

    @Rule
    public ActivityScenarioRule<ExhibitsDirectionsActivity> rule = new ActivityScenarioRule<>(ExhibitsDirectionsActivity.class);

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

    @Test
    public void buttonsDisplayed(){
        ActivityScenario<ExhibitsDirectionsActivity> scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.prev_button).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.next_button).getVisibility());
        });
    }

    @Test
    public void labelsDisplayed(){
        ActivityScenario<ExhibitsDirectionsActivity> scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.prev_button_label).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.next_button_label).getVisibility());
        });
    }

    @Test
    public void titleDisplayed() {
        ActivityScenario<ExhibitsDirectionsActivity> scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.directions_page).getVisibility());
        });
    }

    @Test
    public void directionComponentsDisplayed() {
        ActivityScenario<ExhibitsDirectionsActivity> scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.direction_steps).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.dest_loc).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.dest_dist).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.dest_name).getVisibility());

        });
    }

    @Test
    public void buttonsDisabled() {
        ActivityScenario<ExhibitsDirectionsActivity> scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(false, activity.findViewById(R.id.prev_button).isEnabled());
            assertEquals(false, activity.findViewById(R.id.next_button).isEnabled());
        });
    }

    @Test
    public void settingButtonDisplayed() {
        ActivityScenario<ExhibitsDirectionsActivity> scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(true, activity.findViewById(R.id.settings_btn).isEnabled());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.settings_btn).getVisibility());

        });
    }
}
