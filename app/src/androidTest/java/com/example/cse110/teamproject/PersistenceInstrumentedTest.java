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

@RunWith(AndroidJUnit4.class)
public class PersistenceInstrumentedTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;

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
    public void testPersistence() {
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

        //want to kill app
        scenario.moveToState(Lifecycle.State.DESTROYED);


        //start app again
        ActivityScenario<MainActivity> scenario1
                = ActivityScenario.launch(MainActivity.class);
        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.moveToState(Lifecycle.State.STARTED);
        scenario1.moveToState(Lifecycle.State.RESUMED);

        //now check contents of recyclerview
        onView(withId(R.id.user_list))
                .check(matches(TestUtil.atPosition(0, hasDescendant(withText("Flamingos")))))
                .check(matches(TestUtil.atPosition(1, hasDescendant(withText("Capuchin Monkeys")))));

    }



}
