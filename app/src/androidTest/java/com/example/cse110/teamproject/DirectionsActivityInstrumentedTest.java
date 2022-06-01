package com.example.cse110.teamproject;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.cse110.teamproject.util.MockLocation;
import com.example.cse110.teamproject.util.TestUtil;
import com.google.android.gms.maps.model.LatLng;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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

    /*
    @Test
    public void testOffTrackDirections() {
//    https://stackoverflow.com/questions/8605611/get-context-of-test-project-in-android-junit-test-case
        Context context = ApplicationProvider.getApplicationContext();

        List<String> exhibitIDs = new ArrayList<>(List.of("entrance_exit_gate", "koi", "intxn_front_treetops"));
        List<LatLng> latLngs = TestUtil.convertNodeIDToLatLng(exhibitListItemDao, exhibitIDs);
        Map<String, LatLng> nodeIDsToLatLngsMap = TestUtil.zipToMap(exhibitIDs, latLngs);

    }
     */

    @Test
    public void testDirectionsReplanAccepted() {
//    https://stackoverflow.com/questions/8605611/get-context-of-test-project-in-android-junit-test-case

        Context context = ApplicationProvider.getApplicationContext();

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
                .perform(click(), replaceText("Capuch"));

        onData(equalTo("Capuchin Monkeys"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.plan_btn))
                .perform(click());

        onView(withId(R.id.directions_btn))
                .perform(click());

        // Originally expects Siamangs
        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Siamangs", 5000));

        // Monkey Trail /
        onView(withId(R.id.longitude_input))
                .perform(replaceText("-117.16066409380507"));

        onView(withId(R.id.latitude_input))
                .perform(replaceText("32.74213959255212"));

        onView(withId(R.id.set_location_btn))
                .perform(click());

        // Accept replan
        onView(withId(android.R.id.button1)).perform(click());

        // Now expects Hippos
        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Hippos", 5000));

    }





    @Test
    public void testDirectionsReplanRejected() {
//    https://stackoverflow.com/questions/8605611/get-context-of-test-project-in-android-junit-test-case
        Context context = ApplicationProvider.getApplicationContext();

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
                .perform(click(), replaceText("Capuch"));

        onData(equalTo("Capuchin Monkeys"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        onView(withId(R.id.plan_btn))
                .perform(click());

        onView(withId(R.id.directions_btn))
                .perform(click());

        // Entrance and Exit Gate
        onView(withId(R.id.longitude_input))
                .perform(replaceText("-117.14936"));

        onView(withId(R.id.latitude_input))
                .perform(replaceText("32.73561"));

        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Siamangs'", 5000));

        onView(withId(R.id.set_location_btn))
                .perform(click());

        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Siamangs'", 5000));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Directions to Hippos
        onView(withId(R.id.next_button))
                .perform(click());

        Log.d("<alertdialog>", "326");
        Log.d("<alertdialog>", String.valueOf(android.R.id.message));

        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Hippos'", 5000));

        // Monkey Trail / Hippo Trail
        onView(withId(R.id.longitude_input))
                .perform(replaceText("-117.16951754140803"));

        onView(withId(R.id.latitude_input))
                .perform(replaceText("32.748983757472594"));

        onView(withId(R.id.set_location_btn))
                .perform(click());

//        onView(withId(android.R.id.button1)).perform(click());    // CORRESPONDS TO YES
        onView(withId(android.R.id.button2)).perform(click());      // CORRESPONDS TO NO
//        onView(withId(android.R.id.button3)).perform(click());

        // Target location is still the same: Hippos.
        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Hippos'", 5000));

        // Directions to Capuchin
        onView(withId(R.id.next_button))
                .perform(click());

        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Capuchin", 5000));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Scripps Aviary
        onView(withId(R.id.longitude_input))
                .perform(replaceText("-117.17255093386991"));

        onView(withId(R.id.latitude_input))
                .perform(replaceText("32.748538318135594"));

        onView(withId(R.id.set_location_btn))
                .perform(click());

        onView(withId(android.R.id.button2)).perform(click());      // CORRESPONDS TO NO

        // If refused, there should be no change to the target exhibit
        onView(withId(R.id.direction_steps)).perform(waitForText("to 'Capuchin", 5000));
        Log.d("<alertdialog>", String.valueOf(android.R.id.message));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

}
