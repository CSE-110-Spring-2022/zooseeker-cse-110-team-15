package com.example.cse110.teamproject;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.cse110.teamproject.util.WaitForTextActionKt.waitForText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;

import static java.util.concurrent.TimeUnit.*;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cse110.teamproject.util.MockLocation;
import com.example.cse110.teamproject.util.TestUtil;
import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * Instrumented oldDataTest, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DirectionsActivityInstrumentedTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;
    MockLocation mockLocation;
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp()  {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries().build();

        List<ExhibitNodeItem> nodes = ExhibitNodeItem
                .loadJSON(context, "zoo_node_info.json");
        exhibitListItemDao = testDb.exhibitListItemDao();
        exhibitListItemDao.insertAll(nodes);
        userExhibitListItemDao = testDb.userExhibitListItemDao();

        ExhibitDatabase.injectTestDatabase(testDb);

        mockLocation = new MockLocation(context);
    }

    @After
    public void tearDown() {
        ExhibitDatabase.resetSingleton();
        testDb.close();
    }

    public static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

    @Test
    public void testOffTrackDirections() {
//    https://stackoverflow.com/questions/8605611/get-context-of-test-project-in-android-junit-test-case
        Context context = ApplicationProvider.getApplicationContext();

        List<String> exhibitIDs = new ArrayList<>(List.of("entrance_exit_gate", "koi", "intxn_front_treetops"));
        List<LatLng> latLngs = TestUtil.convertNodeIDToLatLng(exhibitListItemDao, exhibitIDs);
        Map<String, LatLng> nodeIDsToLatLngsMap = TestUtil.zipToMap(exhibitIDs, latLngs);

        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        onView(withId(R.id.search_bar))
                .perform(click(), replaceText("Flami"));

        onData(equalTo("Flamingos"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.search_bar))
                .perform(click(), replaceText("Capuch"));

        onData(equalTo("Capuchin Monkeys"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.plan_btn))
                .perform(click());

        onView(withId(R.id.directions_btn))
                .perform(click());

        // check gate path is in list
        mockLocation.setCurrLocation(nodeIDsToLatLngsMap.get("entrance_exit_gate"));

        onView(withId(R.id.direction_steps)).perform(waitForText("Gate Path", 5000));

        // check gate path is still in list when still on path
        mockLocation.setCurrLocation(nodeIDsToLatLngsMap.get("intxn_front_treetops"));

        onView(withId(R.id.direction_steps)).perform(waitForText("Gate Path", 5000));

        // move to koi and check koi is in path
        mockLocation.setCurrLocation(nodeIDsToLatLngsMap.get("koi"));

        onView(withId(R.id.direction_steps)).perform(waitForText("Koi Fish", 5000));
    }
}
