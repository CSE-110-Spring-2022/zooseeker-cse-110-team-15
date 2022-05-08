package com.example.cse110.teamproject;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PlanActivityInstrumentedTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries().build();
        ExhibitDatabase.injectTestDatabase(testDb);

        exhibitListItemDao = testDb.exhibitListItemDao();
        List<ExhibitNodeItem> nodes = ExhibitNodeItem
                .loadJSON(context, "sample_node_info.json");
        exhibitListItemDao.insertAll(nodes);

        userExhibitListItemDao = testDb.userExhibitListItemDao();
        userExhibitListItemDao.deleteUserExhibitItems();
    }

    public static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

    @After
    public void tearDown() {
        testDb.close();
    }

    @Test
    public void testPlannedExhibits() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        onView(withId(R.id.search_bar))
                .perform(click(), replaceText("Alli"));

        onData(equalTo("Alligators"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.search_bar))
                .perform(click(), replaceText("Gorill"));

        onData(equalTo("Gorillas"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.plan_btn))
                .perform(click());

        onView(withId(R.id.plan_items))
                .check(matches(TestUtil.atPosition(0, hasDescendant(withText("Alligators")))))
                .check(matches(TestUtil.atPosition(0, hasDescendant(withText("Reptile Road")))))
                .check(matches(TestUtil.atPosition(0, hasDescendant(withText("110.0 ft.")))))
                .check(matches(TestUtil.atPosition(1, hasDescendant(withText("Gorillas")))))
                .check(matches(TestUtil.atPosition(1, hasDescendant(withText("Africa Rocks Street")))))
                .check(matches(TestUtil.atPosition(1, hasDescendant(withText("410.0 ft.")))));

    }
}
