package com.example.cse110.teamproject;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.Rule;



@RunWith(AndroidJUnit4.class)
public class SearchResultsActivityTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;

    final Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    //Intent intent = new Intent(targetContext, SearchResultsActivity.class);
    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), SearchResultsActivity.class);
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
    public void BackButtonIsDisplayed(){
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.back_to_search_btn).getVisibility());
        });
        ExhibitDatabase.resetSingleton();
    }


    @Test
    public void SearchTextIsDisplayed(){
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.search_results).getVisibility());
        });
        ExhibitDatabase.resetSingleton();
    }
}

