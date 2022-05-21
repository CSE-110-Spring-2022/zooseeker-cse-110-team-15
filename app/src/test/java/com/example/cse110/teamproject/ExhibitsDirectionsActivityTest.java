package com.example.cse110.teamproject;


import static org.junit.Assert.assertEquals;

import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ExhibitsDirectionsActivityTest {

    @Rule
    public ActivityScenarioRule<ExhibitsDirectionsActivity> rule = new ActivityScenarioRule<>(ExhibitsDirectionsActivity.class);

    @Test
    public void buttonsDisplayed(){
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.prev_button).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.next_button).getVisibility());
        });
        ExhibitDatabase.resetSingleton();
    }

    @Test
    public void labelsDisplayed(){
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.prev_button_label).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.next_button_label).getVisibility());
        });
        ExhibitDatabase.resetSingleton();
    }

    @Test
    public void titleDisplayed() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.directions_page).getVisibility());
        });
        ExhibitDatabase.resetSingleton();
    }

    @Test
    public void directionComponentsDisplayed() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(View.VISIBLE, activity.findViewById(R.id.direction_steps).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.dest_loc).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.dest_dist).getVisibility());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.dest_name).getVisibility());

        });
        ExhibitDatabase.resetSingleton();
    }

    @Test
    public void buttonsDisabled() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(false, activity.findViewById(R.id.prev_button).isEnabled());
            assertEquals(false, activity.findViewById(R.id.next_button).isEnabled());

        });
        ExhibitDatabase.resetSingleton();
    }

    @Test
    public void settingButtonDisplayed() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(true, activity.findViewById(R.id.settings_btn).isEnabled());
            assertEquals(View.VISIBLE, activity.findViewById(R.id.settings_btn).getVisibility());

        });
        ExhibitDatabase.resetSingleton();
    }
}
