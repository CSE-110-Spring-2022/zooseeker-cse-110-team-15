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
import com.example.cse110.teamproject.UserExhibitListItemDao;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// stores path and changes it
public class PathManager implements LocationObserver {

    List<PathInfo> paths;
    Location currentLocation;
    private int currentDirectionIndex;
    Context context;

    ExhibitListItemDao exhibitListItemDao;
    UserExhibitListItemDao userExhibitListItemDao;

    public PathManager(Context context) {
        exhibitListItemDao = ExhibitDatabase.getSingleton(context)
                .exhibitListItemDao();
        userExhibitListItemDao = ExhibitDatabase.getSingleton(context)
                .userExhibitListItemDao();
        paths = PathFinder.findPath(context);
        pathChangeObservers = new ArrayList<>();
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

    // call userOffTrack with current location
    public boolean userOffTrack() {
        return userOffTrack(currentLocation);
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
            Log.d("recalculate_exhibits", "recalculating exhibits + location:" + currVertexLocation);
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

    List<PathChangeObserver> pathChangeObservers;

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




    //
//    public void notifyUserOffTrack(GraphPath<String, IdentifiedWeightedEdge> currentPath) {
//        // TODO
//        // notifyOnTrackObservers() (for US3 Notify and Replan)
//        for (PathChangeObserver o : pathChangeObservers) {
//            o.update(currentPath);
//        }
//
//    }


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
        userOffTrack(currentLocation);
    }


    public void updateCurrentDirectionIndex(int directionOrder) {
        Log.d("<location>", "updateCurrentDirectionIndex called");
        this.currentDirectionIndex = directionOrder;
    }
}
