package com.example.cse110.teamproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cse110.teamproject.path.PathFinder;
import com.example.cse110.teamproject.path.PathManager;

import org.jgrapht.GraphPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PathManagerTest {
    ExhibitDatabase testDb;
    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;
    PathItemDao pathItemDao;

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp()  {
        Context context = ApplicationProvider.getApplicationContext();
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
    public void testPathChange() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {

            // path initialization
            userExhibitListItemDao.deleteUserExhibitItems();

            userExhibitListItemDao.insert(new UserExhibitListItem("flamingo"));
            userExhibitListItemDao.insert(new UserExhibitListItem("koi"));
            userExhibitListItemDao.insert(new UserExhibitListItem("gorilla"));

            List<GraphPath<String,IdentifiedWeightedEdge>> paths = PathFinder.findPath(activity);
            int currentDirectionIndex = 0;
            GraphPath<String,IdentifiedWeightedEdge> currentPath = paths.get(currentDirectionIndex);
            List<String> currentPathVertices = currentPath.getVertexList();

            // check original graph
            assertEquals(currentPathVertices.get(0), "entrance_exit_gate");
            assertEquals(currentPathVertices.get(1), "intxn_front_treetops");
            assertEquals(currentPathVertices.get(2), "intxn_front_lagoon_1");
            assertEquals(currentPathVertices.get(3), "koi");

            // set initial user location to (approximately) the exhibit entrance
            Location initialUserLocation = new Location("");
            initialUserLocation.setLongitude(-117.14936);
            initialUserLocation.setLatitude(32.73561);

            // init path manager
            PathManager pathManager = new PathManager(activity);
            pathManager.updateCurrentDirectionIndex(currentDirectionIndex);

            MockUserLocation mockUserLocation = new MockUserLocation((ComponentActivity) activity, initialUserLocation);
            mockUserLocation.addLocationChangedObservers(pathManager);

            assertFalse(pathManager.userOffTrack(mockUserLocation.getUserLocation()));

            // set second user location to (approximately) the next exhibit in the path
            Location nextOnTrackUserLocation = new Location("");
            nextOnTrackUserLocation.setLongitude(-117.15496383006723);      // id: intxn_front_lagoon_1
            nextOnTrackUserLocation.setLatitude(32.72726737662313);
            mockUserLocation.setUserLocation(nextOnTrackUserLocation);

            assertFalse(pathManager.userOffTrack(mockUserLocation.getUserLocation()));

            // set third user location to an exhibit not in the current path
            Location offTrackUserLocation = new Location("");
            offTrackUserLocation.setLongitude(-117.172699);
            offTrackUserLocation.setLatitude(32.734809);
            mockUserLocation.setUserLocation(offTrackUserLocation);

            assertFalse(pathManager.userOffTrack(mockUserLocation.getUserLocation()));

            // check original loaded directions
            List<PathItem> directions = pathItemDao.getAll();

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

            // check new stored path changes
            paths = pathManager.getPath();
            currentPath = paths.get(currentDirectionIndex);
            currentPathVertices = currentPath.getVertexList();
            assertEquals(currentPathVertices.get(0), "fern_canyon");
            assertEquals(currentPathVertices.get(1), "intxn_treetops_fern_trail");
            assertEquals(currentPathVertices.get(2), "intxn_front_treetops");
            assertEquals(currentPathVertices.get(3), "intxn_front_lagoon_1");
            assertEquals(currentPathVertices.get(4), "koi");

        });
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


}
