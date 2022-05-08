package com.example.cse110.teamproject;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class PlanActivityTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;

    final Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    //Intent intent = new Intent(targetContext, SearchResultsActivity.class);
    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), PlanActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("YOUR_BUNDLE_TAG", "YOUR_VALUE");
        intent.putExtras(bundle);
    }


    @Rule
    public ActivityScenarioRule<SearchResultsActivity> rule = new ActivityScenarioRule<>(intent);

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries().build();
        exhibitListItemDao = testDb.exhibitListItemDao();
        userExhibitListItemDao = testDb.userExhibitListItemDao();
    }

    @After
    public void tearDown() {
        testDb.close();
    }


    @Test
    public void DirectionsButtonIsDisplayed(){
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.directions_btn).getVisibility());
        });
        ExhibitDatabase.resetSingleton();
    }

    @Test
    public void recyclerViewIsVisible() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.plan_items).getVisibility());
        });
        ExhibitDatabase.resetSingleton();
    }


//    @Test
//    public void SearchTextIsDisplayed(){
//        ActivityScenario scenario = rule.getScenario();
//        scenario.moveToState(Lifecycle.State.CREATED);
//
//        scenario.onActivity(activity -> {
//            assertEquals(View.VISIBLE, activity.findViewById(R.id.search_results).getVisibility());
//        });
//        ExhibitDatabase.resetSingleton();
//    }
}

