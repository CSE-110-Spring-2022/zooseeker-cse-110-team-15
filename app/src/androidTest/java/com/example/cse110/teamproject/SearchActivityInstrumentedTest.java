package com.example.cse110.teamproject;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SearchActivityInstrumentedTest {
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
        exhibitListItemDao = testDb.exhibitListItemDao();
        userExhibitListItemDao = testDb.userExhibitListItemDao();
    }

    public static void forceLayout(RecyclerView recyclerView) {
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

    @After
    public void tearDown() {
        testDb.close();
    }

    // https://stackoverflow.com/questions/31394569/how-to-assert-inside-a-recyclerview-in-espresso
    // CC BY-SA 4.0.
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    private static void checkNotNull(Matcher<View> itemMatcher) {
    }

    @Test
    public void testSearchExhibit() {
        ActivityScenario<MainActivity> scenario
                = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

//        scenario.onActivity(activity -> {
            onView(withId(R.id.search_bar))
                    .perform(click(), replaceText("Alli"))
                    .check(matches(withText("Alli")));
//
//            AutoCompleteTextView searchBar = activity.findViewById(R.id.search_bar);
////            searchBar.requestFocus();
//            searchBar.setText("Alligators");
//            searchBar.showDropDown();
            onData(equalTo("Alligators"))
                    .inRoot(RootMatchers.isPlatformPopup())
                    .perform(click());

            onView(withId(R.id.user_list))
                .check(matches(atPosition(0, hasDescendant(withText("Alligators")))));

//            searchBar.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    searchBar.setText("PREM");
//                    searchBar.showDropDown();
//
//                }
//            }, 10);
//            searchBar.clearFocus();

//            final String[] result = new String[1];
//            searchBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                    result[0] = adapterView.getItemAtPosition(i).toString();
//                    assertEquals("Alligators", result[0]);
//                    view.performClick();
//
//
//                    searchBar.setListSelection(0);
//                    searchBar.performClick();
//
//                    // test adding to list
//
////            assertEquals(1,userExhibitListItemDao.getListSize());
//
//                    RecyclerView userListRecycler = activity.userListRecycler;
//                    String text = ((TextView) userListRecycler.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.user_list)).getText().toString();
//                    long firstUserListItemID = userListRecycler.getAdapter().getItemId(0);
//                    TextView firstUserListItem = activity.findViewById((int) firstUserListItemID);
//                    assertEquals("Alligators", text);
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> adapterView) {}
//            });


//            ArrayAdapter<String> dropDownAdapter = activity.arrayAdapter;
//            long firstResultID = dropDownAdapter.getItemId(0);
//            TextView firstResult = activity.findViewById(firstResultID);
//
//            String exhibit = (String) firstResult.getText();
//            assertEquals("Alligators", exhibit);
//        });
    }

//    @Test
//    public void testAddExhibitToList() {
//        ActivityScenario<MainActivity> scenario
//                = ActivityScenario.launch(MainActivity.class);
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.moveToState(Lifecycle.State.STARTED);
//        scenario.moveToState(Lifecycle.State.RESUMED);
//
//        scenario.onActivity(activity -> {
//            EditText searchBar = activity.findViewById(R.id.search_bar);
//            searchBar.requestFocus();
//            searchBar.setText("Alligators");
//            searchBar.clearFocus();
//
//            searchBar.
//                    RecyclerView recyclerView = activity.;
//            RecyclerView.ViewHolder firstVH = recyclerView.findViewHolderForAdapterPosition(0);
//            assertNotNull(firstVH);
//            long id = firstVH.getItemId();
//            TextView firstResult = firstVH.itemView.findViewById(R.id.search_bar);
//
//            String exhibit = (String) firstResult.getText();
//            assertEquals("Alligators", exhibit);
//    }
}