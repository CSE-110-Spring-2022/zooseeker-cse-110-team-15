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
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.activity.ComponentActivity;
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
    MockUserLocation mockUserLocation;
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

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        onView(withId(R.id.direction_steps)).perform(waitForText("Gate Path", 5000));

        // check gate path is still in list when still on path
        mockLocation.setCurrLocation(nodeIDsToLatLngsMap.get("intxn_front_treetops"));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        onView(withId(R.id.direction_steps)).perform(waitForText("Gate Path", 5000));

        // move to koi and check koi is in path
        mockLocation.setCurrLocation(nodeIDsToLatLngsMap.get("koi"));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 10; i++) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mockLocation.setCurrLocation(nodeIDsToLatLngsMap.get("koi"));

//        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Hippos'", 5000));

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mockLocation.setCurrLocation(nodeIDsToLatLngsMap.get("intxn_front_treetops"));

        }

//        onView(withId(R.id.direction_steps)).perform(waitForText("Koi Fish", 5000));
    }

    @Test
    public void testReplannedDirections() {
//    https://stackoverflow.com/questions/8605611/get-context-of-test-project-in-android-junit-test-case
        Context context = ApplicationProvider.getApplicationContext();

        List<String> exhibitIDs = new ArrayList<>(List.of("entrance_exit_gate", "intxn_hippo_monkey_trails", "scripps_aviary"));
        List<LatLng> latLngs = TestUtil.convertNodeIDToLatLng(exhibitListItemDao, exhibitIDs);
        Map<String, LatLng> nodeIDsToLatLngsMap = TestUtil.zipToMap(exhibitIDs, latLngs);

        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);


        onView(withId(R.id.search_bar))
                .perform(click(), replaceText("Siam"));

        onData(equalTo("Siamangs"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.search_bar))
                .perform(click(), replaceText("Hi"));

        onData(equalTo("Hippos"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.search_bar))
                .perform(click(), replaceText("Gor"));

        onData(equalTo("Gorillas"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.search_bar))
                .perform(click(), replaceText("Flam"));

        onData(equalTo("Flamingos"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.plan_btn))
                .perform(click());

        onView(withId(R.id.directions_btn))
                .perform(click());

        // check gate path is in list
        LatLng locationLatLng = nodeIDsToLatLngsMap.get("entrance_exit_gate");
        Location location = new Location("");
        location.setLongitude(locationLatLng.longitude);
        location.setLatitude(locationLatLng.latitude);
        mockUserLocation = new MockUserLocation(context, location);
        mockUserLocation.setUserLocation(location);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //onView(withId(R.id.direction_steps)).perform(waitForText("to 'Siamangs'", 5000));

        // check gate path is still in list when still on path

        onView(withId(R.id.next_button))
                .perform(click());

        onView(withId(R.id.next_button))
                .perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        locationLatLng = nodeIDsToLatLngsMap.get("intxn_hippo_monkey_trails");
        location = new Location("");
        location.setLongitude(locationLatLng.longitude);
        location.setLatitude(locationLatLng.latitude);
        mockUserLocation.setUserLocation(location);
        Log.d("<location>", "location changed");

//        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Hippos'", 5000));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // move to koi and check koi is in path
        locationLatLng = nodeIDsToLatLngsMap.get("scripps_aviary");
        location = new Location("");
        location.setLongitude(locationLatLng.longitude);
        location.setLatitude(locationLatLng.latitude);
        mockUserLocation.setUserLocation(location);
        Log.d("<location>", "location changed");

        for (int i = 0; i < 10; i++) {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            locationLatLng = nodeIDsToLatLngsMap.get("intxn_hippo_monkey_trails");
            location = new Location("");
            location.setLongitude(locationLatLng.longitude);
            location.setLatitude(locationLatLng.latitude);
            mockUserLocation.setUserLocation(location);
            Log.d("<location>", "location changed");

//        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Hippos'", 5000));

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // move to koi and check koi is in path
            locationLatLng = nodeIDsToLatLngsMap.get("scripps_aviary");
            location = new Location("");
            location.setLongitude(locationLatLng.longitude);
            location.setLatitude(locationLatLng.latitude);
            mockUserLocation.setUserLocation(location);
            Log.d("<location>", "location changed");

        }

//        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Gorillas'", 5000));
    }

}
