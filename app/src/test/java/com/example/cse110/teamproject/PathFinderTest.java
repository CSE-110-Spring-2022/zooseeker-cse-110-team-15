package com.example.cse110.teamproject;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cse110.teamproject.path.PathFinder;

import org.jgrapht.GraphPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PathFinderTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;
    PathItemDao pathItemDao;
    Context context;

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp()  {
        context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries().build();

        List<ExhibitNodeItem> nodes = ExhibitNodeItem
                .loadJSON(context, "zoo_node_info.json");
        System.out.println("nodes from json: " + nodes.toString());
        exhibitListItemDao = testDb.exhibitListItemDao();
        exhibitListItemDao.insertAll(nodes);
        userExhibitListItemDao = testDb.userExhibitListItemDao();
        pathItemDao = testDb.pathItemDao();

        ExhibitDatabase.injectTestDatabase(testDb);
    }

    @After
    public void tearDown() {
        ExhibitDatabase.resetSingleton();
        testDb.close();
    }

    @Test
    public void testFindPath() {

        // lions must be visited to get to elephant_odyssey
        // entrance_exit_gate -> entrance_plaza -> gators (INSTEAD OF gorillas) -> lions -> elephant_odyssey
        // ... -> entrance_plaza -> arctic_foxes

        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {

            userExhibitListItemDao.deleteUserExhibitItems();

            userExhibitListItemDao.insert(new UserExhibitListItem("flamingo"));
            userExhibitListItemDao.insert(new UserExhibitListItem("koi"));
            userExhibitListItemDao.insert(new UserExhibitListItem("gorilla"));

            PathFinder.findPath(activity);

//            System.out.println("get all exhibits: ");
//            System.out.println(exhibitListItemDao.getAllExhibits());
//            System.out.println("get all user exhibits: ");
//            System.out.println(userExhibitListItemDao.getAllUserExhibits());
//            System.out.println("get all path items: ");
//            System.out.println(pathItemDao.getAll());

            List<PathItem> directions = pathItemDao.getAll();

            System.out.println("exhibits: " + directions);

            PathItem firstPathItem = directions.get(0);
            assertEquals("koi", firstPathItem.node_id);
            List<String> firstExhibitDirections = firstPathItem.curr_directions;
            assertEquals("gate_to_front", firstExhibitDirections.get(0));
            assertEquals("front_to_lagoon1", firstExhibitDirections.get(1));
            assertEquals("lagoon1_to_koi", firstExhibitDirections.get(2));

            PathItem secondPathItem = directions.get(1);
            assertEquals("flamingo", secondPathItem.node_id);
            List<String> secondExhibitDirections = secondPathItem.curr_directions;
            String[] expectedDirections2 = new String[]{"lagoon1_to_koi", "front_to_lagoon1", "front_to_monkey", "monkey_to_flamingo"};
            assertEquals(new ArrayList<>(Arrays.asList(expectedDirections2)), secondExhibitDirections);

            PathItem thirdPathItem = directions.get(2);
            assertEquals("gorilla", thirdPathItem.node_id);
            List<String> thirdExhibitDirections = thirdPathItem.curr_directions;
            String[] expectedDirections3 = new String[]{"flamingo_to_capuchin", "capuchin_to_hippo_monkey", "hippo_monkey_to_scripps", "scripps_to_gorilla"};
            assertEquals(new ArrayList<>(Arrays.asList(expectedDirections3)), thirdExhibitDirections);
        });
    }

    @Test
    public void testFindPathToFixedNextWorksForExhibitsInSameVertex() {
        // paths from two exhibits in same vertex should just have itself as the vertex
        GraphPath<String, IdentifiedWeightedEdge> path = PathFinder.findPathToFixedNext(context, "dove", "mynah");
        String[] expectedDirections = new String[]{"owens_aviary"};
        assertEquals(new ArrayList<>(Arrays.asList(expectedDirections)), path.getVertexList());
    }
}
