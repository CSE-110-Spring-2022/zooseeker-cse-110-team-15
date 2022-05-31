package com.example.cse110.teamproject.path;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;

import com.example.cse110.teamproject.ExhibitDatabase;
import com.example.cse110.teamproject.ExhibitListItemDao;
import com.example.cse110.teamproject.ExhibitNodeItem;
import com.example.cse110.teamproject.IdentifiedWeightedEdge;
import com.example.cse110.teamproject.LocationObserver;
import com.example.cse110.teamproject.UserOffTrackObserver;
import com.example.cse110.teamproject.UserExhibitListItemDao;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// stores path and changes it
public class PathManager implements LocationObserver {

    List<PathInfo> paths;
    Location currentLocation;
    private int currentDirectionIndex;
    Context context;

    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;

    List<PathChangeObserver> pathChangeObservers;
    List<UserOffTrackObserver> userOffTrackObservers;

    //ReplanNotification replanNotification;

    public PathManager(Context context) {
        exhibitListItemDao = ExhibitDatabase.getSingleton(context)
                .exhibitListItemDao();
        userExhibitListItemDao = ExhibitDatabase.getSingleton(context)
                .userExhibitListItemDao();
        paths = PathFinder.findPath(context);
        pathChangeObservers = new ArrayList<>();
        userOffTrackObservers = new ArrayList<>();
        this.context = context;
        Location loc = new Location("");
        loc.setAltitude(100);
        loc.setLatitude(0);
        loc.setLongitude(0);
        loc.setTime(System.currentTimeMillis());
        loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        loc.setAccuracy(6f);
        currentLocation = loc;
        Log.d("test", currentLocation.toString());
    }

    public String currentVertexLocation(Location location) {
        // get user location
        // get distance to all points

        Pair<String, Float> closest = new Pair<String, Float>("", Float.POSITIVE_INFINITY);

        List<ExhibitNodeItem> allNodes = exhibitListItemDao.getAllNodes();
        for (ExhibitNodeItem exhibitNodeItem : allNodes) {
            float distanceBetween = distanceBetween(location, exhibitNodeItem.lat, exhibitNodeItem.lng);
            if (distanceBetween < closest.second) {
                closest = new Pair<String, Float>(exhibitNodeItem.node_id, distanceBetween);
            }
        }

        return closest.first;
    }

    /**
     * Calculates distance between the current user location and a given exhibit given by (lat,lng)
     *
     * @param location User location
     * @param lat Latitude of exhibit
     * @param lng Longitude of exhibit
     * @return Distance between user and the exhibit
     */
    public float distanceBetween(Location location, float lat, float lng) {
        Location exhibitLocation = new Location("");
        exhibitLocation.setLatitude(lat);
        exhibitLocation.setLongitude(lng);
        return exhibitLocation.distanceTo(location);
    }

    // off track is determined in relation to the current directions page the user is on.
    public boolean userOffTrack(Location currentLocation) {
        Log.d("<location>", "userOffTrack called");
        GraphPath<String,IdentifiedWeightedEdge> currentPath = paths.get(currentDirectionIndex).getPath();
        List<String> currentPathVertices = currentPath.getVertexList();
        String currVertexLocation = currentVertexLocation(currentLocation);
        Log.d("<userLocation>", "user location set to (" + currentLocation.getLongitude() + ", " + currentLocation.getLatitude() + ").");
        Log.d("<user location>", currVertexLocation);
        // if user is not at vertex on current path
        if (currentPathVertices.indexOf(currVertexLocation) == -1) {
            recalculateToExhibit(currVertexLocation, currentPath.getEndVertex());
            return true;
        }
        // logic for determining if user is on or off track; calls userOnTrack if necessary
        // if currentLocation corres. to some vertex in GraphPath do nothing
        // if currentLocation corres. to no vertices in GraphPath call notifyOffTrack()
        return false;
    }

    /**
     * Updates path with modified path to the current exhibit (observes currentDirectionIndex).
     * Called automatically with observation of userOffTrack changes.
     *
     * @param currVertexLocation Current location of the user (as a vertex)
     * @param nextExhibitID The ID of the fixed next exhibit in the directions
     */
    private void recalculateToExhibit(String currVertexLocation, String nextExhibitID) {
        recalculateToExhibit(currVertexLocation, nextExhibitID, currentDirectionIndex);
        Log.d("<recalculation>", "recalculateToExchibit called");
    }

    private void recalculateToExhibit(String currVertexLocation, String nextExhibitID, int locationIndex) {
        PathInfo path = paths.get(locationIndex);
        path.setPath(PathFinder.findPathToFixedNext(context, currVertexLocation, nextExhibitID));
        Log.d("<recalculation>", "recalculateToExchibit called");
        notifyPathChanged();
    }

    /**
     * Updates path with modified path given that there is a different closest exhibit that was
     * added to the plan. Any previous exhibits in the directions are unchanged. However the
     * current exhibit referred to in directions is changed, and the following exhibits in the
     * directions may also have changed indices. In other words, the findPath method is
     * effectively run with the restriction of operating on the current vertex and on strictly
     * following vertices, i.e., the omission of previous nodes.
     *
     * Note that the ReplanNotification class should handle the determination of whether or not
     * to call this method
     *
     * @param currVertexLocation Current location of the user (as a vertex)
     */
    public void recalculateOverall(String currVertexLocation) {
        List<String> nodesToOmit = new ArrayList<>();
        for (int i = 0; i < currentDirectionIndex; i++) {
            // objective vertex of the current directions page represented by end vertex of current path
            nodesToOmit.add(paths.get(i).getPath().getEndVertex());
        }
        // recalculate latter path of the path
        List<PathInfo> latterPathSegment =
                PathFinder.findPathGivenExcludedNodes(context, currVertexLocation, nodesToOmit);

        // concatenate latter part of the path [curr, curr + recalc_len] to the former (indices [0, curr-1]), and return resulting path
        for (int i = currentDirectionIndex; i < currentDirectionIndex + latterPathSegment.size(); i++) {
            paths.set(i, latterPathSegment.get(i - currentDirectionIndex));
            Log.d("test", i + String.valueOf(latterPathSegment.size()));
        }
        notifyPathChanged();
    }

    public void notifyPathChanged() {
        Log.d("path_update", "updated" + paths.toString());
        Log.d("<pathChangeObservers>", pathChangeObservers.toString());
        for (PathChangeObserver o : pathChangeObservers) {
            o.update(paths);
        }
    }

    // ReplanNotification class handles this observation/logic
    public void notifyPathChangedWithDiffFirst() {

    }

    public void addPathChangeObserver(PathChangeObserver o) {
        pathChangeObservers.add(o);
    }

    public List<PathInfo> getPath() {
        return this.paths;
    }

    public void addUserOffTrackObserver(UserOffTrackObserver o) {
        userOffTrackObservers.add(o);
    }

    public void notifyUserOffTrack(String currentVertexLocation) {
        // TODO
        // notifyOnTrackObservers() (for US3 Notify and Replan)
        for (UserOffTrackObserver o : userOffTrackObservers) {
            o.update(currentVertexLocation);
        }

    }


    /**
     * Implementation of the observer interface method for user location changes.
     * Automatically class userOffTrack, which continuously updates the directions to the
     * fixed next exhibit as the user location changes.
     *
     * @param currentLocation Current user location (as Location)
     */
    public void updateLocation(Location currentLocation) {
        Log.d("<location>", "updateLocation called");
        this.currentLocation = currentLocation;
        //userOffTrack(currentLocation);
        replanPath(currentLocation);
    }


    public void updateCurrentDirectionIndex(int directionOrder) {
        Log.d("<location>", "updateCurrentDirectionIndex called");
        this.currentDirectionIndex = directionOrder;
    }

    // Check if user is substantially off-track and re-plan if so.
    public void replanPath(Location currentLocation) {
        boolean userReaction = false;
        boolean isUserCloserToLaterExhibits = false;

        PathInfo currentPath = paths.get(currentDirectionIndex);
        String currentVertexLocation = currentVertexLocation(currentLocation);

        List<String> laterExhibits = new ArrayList<>();

        // get all the later paths
        for (int i = currentDirectionIndex + 1; i < paths.size(); i++) {
            // laterPaths will be empty if currentDirection is the last exhibit to visit
            laterExhibits.add(paths.get(i).getPath().getEndVertex());
        }

        // get distance between current location and current destination exhibit
        ExhibitNodeItem currentExhibitNode = exhibitListItemDao.getExhibitByNodeId(currentPath.getPath().getEndVertex());
        float distanceToCurrExhibit = distanceBetween(currentLocation, currentExhibitNode.lat, currentExhibitNode.lng);

        // check if distance between current location and later exhibits is shorter than
        // the distance between current location and current exhibit
        for (int i = 0; i < laterExhibits.size(); i++) {
            ExhibitNodeItem laterNode = exhibitListItemDao.getExhibitByNodeId(laterExhibits.get(i));
            float distance = distanceBetween(currentLocation, laterNode.lat, laterNode.lng);
            if (distance < distanceToCurrExhibit) {
                isUserCloserToLaterExhibits = true;
                break;
            }
        }

        // if the user is off-track and is closer to the later exhibits in the path
        if (userOffTrack(currentLocation) && isUserCloserToLaterExhibits) {
            // then ask them if they want to replan their path
            notifyUserOffTrack(currentVertexLocation);

            // if yes, re-plan their path so that their closest exhibit is now their next exhibit
            if (userReaction) {
                recalculateOverall(currentVertexLocation);
            }
        }
    }

    public void reverseRoute(int currPathIndex) {
        PathInfo pathInfo = paths.get(currPathIndex);
        PathInfo.Direction newDirection = (pathInfo.getDirection() == PathInfo.Direction.FORWARDS) ? PathInfo.Direction.REVERSE : PathInfo.Direction.FORWARDS;
        pathInfo.setDirection(newDirection);

        GraphPath<String, IdentifiedWeightedEdge> graphPath = pathInfo.getPath();
        String startVertexID = graphPath.getStartVertex();
        String endVertexID = graphPath.getEndVertex();
        recalculateToExhibit(endVertexID, startVertexID, currPathIndex);
    }

    // will make route match direction specified by reversing if necessary
    public void updateRouteDirection(int currPathIndex, PathInfo.Direction direction) {
        PathInfo pathInfo = paths.get(currPathIndex);
        if (!(pathInfo.getDirection() == direction)) {
            reverseRoute(currPathIndex);
        }
    }

}
