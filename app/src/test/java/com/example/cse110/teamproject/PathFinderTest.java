package com.example.cse110.teamproject;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.AutoCompleteTextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.file.Path;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PathFinderTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;
    PathItemDao pathItemDao;

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
//        Context context = ApplicationProvider.getApplicationContext();
//        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
//                .allowMainThreadQueries().build();
//        exhibitListItemDao = testDb.exhibitListItemDao();
//        userExhibitListItemDao = testDb.userExhibitListItemDao();
//        pathItemDao = testDb.pathItemDao();

    }

    @After
    public void tearDown() {
//        testDb.close();
    }

    @Test
    public void testFindPath() {

        // lions must be visited to get to elephant_odyssey
        // entrance_exit_gate -> entrance_plaza -> gators (INSTEAD OF gorillas) -> lions -> elephant_odyssey
        // ... -> entrance_plaza -> arctic_foxes

        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {

            exhibitListItemDao = ExhibitDatabase.getSingleton(activity)
                    .exhibitListItemDao();

            userExhibitListItemDao = ExhibitDatabase.getSingleton(activity)
                    .userExhibitListItemDao();

            pathItemDao = ExhibitDatabase.getSingleton(activity)
                    .pathItemDao();

            userExhibitListItemDao.deleteUserExhibitItems();

            userExhibitListItemDao.insert(new UserExhibitListItem("lions"));
            userExhibitListItemDao.insert(new UserExhibitListItem("elephant_odyssey"));
            userExhibitListItemDao.insert(new UserExhibitListItem("arctic_foxes"));

            // pathItemDao.insert(new PathItem("lions", new ArrayList(), 3));

            PathFinder.findPath(activity);

//            System.out.println("get all exhibits: ");
//            System.out.println(exhibitListItemDao.getAllExhibits());
//            System.out.println("get all user exhibits: ");
//            System.out.println(userExhibitListItemDao.getAllUserExhibits());
//            System.out.println("get all path items: ");
//            System.out.println(pathItemDao.getAll());

            List<PathItem> directions = pathItemDao.getAll();

            PathItem firstPathItem = directions.get(0);
            assertEquals("lions", firstPathItem.node_id);
            List<String> firstExhibitDirections = firstPathItem.curr_directions;
            assertEquals("edge-0", firstExhibitDirections.get(0));
            assertEquals("edge-5", firstExhibitDirections.get(1));
            assertEquals("edge-6", firstExhibitDirections.get(2));

            PathItem secondPathItem = directions.get(1);
            assertEquals("elephant_odyssey", secondPathItem.node_id);
            List<String> secondExhibitDirections = secondPathItem.curr_directions;
            assertEquals("edge-3", secondExhibitDirections.get(0));

            PathItem thirdPathItem = directions.get(2);
            assertEquals("arctic_foxes", thirdPathItem.node_id);
            List<String> thirdExhibitDirections = thirdPathItem.curr_directions;
            assertEquals("edge-3", thirdExhibitDirections.get(0));
            assertEquals("edge-6", thirdExhibitDirections.get(1));
            assertEquals("edge-5", thirdExhibitDirections.get(2));
            assertEquals("edge-4", thirdExhibitDirections.get(3));
        });
        ExhibitDatabase.resetSingleton();
    }


}