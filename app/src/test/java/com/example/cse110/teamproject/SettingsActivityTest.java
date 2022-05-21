package com.example.cse110.teamproject;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.runner.RunWith;

import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.Rule;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {
    @Rule
    public ActivityScenarioRule<SettingsActivity> rule = new ActivityScenarioRule<>(SettingsActivity.class);

    @Test
    public void test1() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            assertEquals(1, 1);
        });
    }
}
