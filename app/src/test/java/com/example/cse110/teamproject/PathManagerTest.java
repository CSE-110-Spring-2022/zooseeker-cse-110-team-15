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
import com.example.cse110.teamproject.path.PathInfo;
import com.example.cse110.teamproject.path.PathManager;

import org.jgrapht.GraphPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

            List<GraphPath<String,IdentifiedWeightedEdge>> paths = PathFinder.findPath(activity).stream().map((pathInfo -> pathInfo.getPath())).collect(Collectors.toList());
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
            paths = pathManager.getPath().stream().map(pathInfo -> pathInfo.getPath()).collect(Collectors.toList());
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
    public void testReversePathReversesPath() {
        ActivityScenario scenario = rule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {

            // path initialization
            userExhibitListItemDao.deleteUserExhibitItems();

            userExhibitListItemDao.insert(new UserExhibitListItem("flamingo"));
            userExhibitListItemDao.insert(new UserExhibitListItem("koi"));
            userExhibitListItemDao.insert(new UserExhibitListItem("gorilla"));

            // init path manager
            PathManager pathManager = new PathManager(activity);

            int currentDirectionIndex = 0;
            List<PathInfo> pathList = pathManager.getPath();
            PathInfo currentPathInfo = pathList.get(currentDirectionIndex);
            List<GraphPath<String,IdentifiedWeightedEdge>> paths = pathList.stream().map((pathInfo -> pathInfo.getPath())).collect(Collectors.toList());
            GraphPath<String,IdentifiedWeightedEdge> currentPath = paths.get(currentDirectionIndex);

            List<String> currentPathVertices = currentPath.getVertexList();

            List<String> expectedPath = new ArrayList<>(List.of("entrance_exit_gate", "intxn_front_treetops", "intxn_front_lagoon_1", "koi"));
            // check original path
            assertEquals(expectedPath, currentPathVertices);
            assertEquals(PathInfo.Direction.FORWARDS, currentPathInfo.getDirection());

            // check path is same after set direction to forward
            // do direction forward
            pathManager.updateRouteDirection(currentDirectionIndex, PathInfo.Direction.FORWARDS);
            assertEquals(expectedPath, currentPathVertices);
            currentPath = paths.get(currentDirectionIndex);
            assertEquals(PathInfo.Direction.FORWARDS, currentPathInfo.getDirection());

            // check reversed path
            // do direction reverse
            pathManager.updateRouteDirection(currentDirectionIndex, PathInfo.Direction.REVERSE);
            paths = pathManager.getPath().stream().map(pathInfo -> pathInfo.getPath()).collect(Collectors.toList());
            currentPathInfo = pathList.get(currentDirectionIndex);
            currentPath = paths.get(currentDirectionIndex);

            assertEquals(PathInfo.Direction.REVERSE, currentPathInfo.getDirection());
            currentPathVertices = currentPath.getVertexList();
            // assert
            Collections.reverse(expectedPath);
            assertEquals(expectedPath, currentPathVertices);

            // check reversed path again
            // do direction reverse
            pathManager.updateRouteDirection(currentDirectionIndex, PathInfo.Direction.FORWARDS);
            paths = pathManager.getPath().stream().map(pathInfo -> pathInfo.getPath()).collect(Collectors.toList());
            currentPath = paths.get(currentDirectionIndex);
            currentPathVertices = currentPath.getVertexList();
            currentPath = paths.get(currentDirectionIndex);
            // assert
            assertEquals(PathInfo.Direction.FORWARDS, currentPathInfo.getDirection());
            Collections.reverse(expectedPath);
            assertEquals(expectedPath, currentPathVertices);
        });
    }
}
