package com.example.cse110.teamproject;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.Rule;

    @RunWith(AndroidJUnit4.class)
    public class SearchActivityTest {
        @Rule
        public ActivityScenarioRule rule = new ActivityScenarioRule<>(MainActivity.class);

        @Test
        public void SearchBarIsDisplayed() {
            ActivityScenario scenario = rule.getScenario();
            scenario.moveToState(Lifecycle.State.CREATED);

            scenario.onActivity(activity -> {
                assertEquals(View.VISIBLE, (activity.findViewById(R.id.search_bar)).getVisibility());
                assertEquals("Search Animal Exhibits", ((AutoCompleteTextView) activity.findViewById(R.id.search_bar)).getHint());
            });
        }

        @Test
        public void SearchTitleIsDisplayed() {
            ActivityScenario scenario = rule.getScenario();
            scenario.moveToState(Lifecycle.State.CREATED);

            scenario.onActivity(activity -> {
                assertEquals("Search", ((TextView) activity.findViewById(R.id.search_page)).getText());
            });
        }

        @Test
        public void SearchIconIsDisplayed() {
            ActivityScenario scenario = rule.getScenario();
            scenario.moveToState(Lifecycle.State.CREATED);

            scenario.onActivity(activity -> {
                assertEquals(View.VISIBLE, ((ImageView) activity.findViewById(R.id.search_icon)).getVisibility());
            });
        }
}
