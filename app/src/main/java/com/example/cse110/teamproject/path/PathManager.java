package com.example.cse110.teamproject.path;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.example.cse110.teamproject.ExhibitDatabase;
import com.example.cse110.teamproject.ExhibitListItemDao;
import com.example.cse110.teamproject.ExhibitNodeItem;
import com.example.cse110.teamproject.IdentifiedWeightedEdge;
import com.example.cse110.teamproject.LocationObserver;

import org.jgrapht.GraphPath;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// stores path and changes it
public class PathManager implements LocationObserver {

    List<GraphPath<String, IdentifiedWeightedEdge>> paths;
    Location currentLocation;
    private int currentDirectionIndex;
    Context context;

    ExhibitListItemDao exhibitListItemDao;

    public PathManager(Context context) {
        exhibitListItemDao = ExhibitDatabase.getSingleton(context)
                .exhibitListItemDao();
        paths = PathFinder.findPath(context);
        pathChangeObservers = new ArrayList<>();
        this.context = context;
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
        GraphPath<String,IdentifiedWeightedEdge> currentPath = paths.get(currentDirectionIndex);
        List<String> currentPathVertices = currentPath.getVertexList();
        String currVertexLocation = currentVertexLocation(currentLocation);
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

    private void recalculateToExhibit(String currVertexLocation, String nextExhibitID) {
        paths.set(currentDirectionIndex, PathFinder.findPathToFixedNext(context, currVertexLocation, nextExhibitID));
    }

    public void notifyPathChanged() {
        for (PathChangeObserver o : pathChangeObservers) {
            o.update(paths);
        }
    }

    public void addPathChangeObserver(PathChangeObserver o) {
        pathChangeObservers.add(o);
    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> getPath() {
        return this.paths;
    }


    List<PathChangeObserver> pathChangeObservers;

    //
//    public void notifyUserOffTrack(GraphPath<String, IdentifiedWeightedEdge> currentPath) {
//        // TODO
//        // notifyOnTrackObservers() (for US3 Notify and Replan)
//        for (PathChangeObserver o : pathChangeObservers) {
//            o.update(currentPath);
//        }
//
//    }

    public void updateLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
        userOffTrack(currentLocation);
        notifyPathChanged();
    }


    public void updateCurrentDirectionIndex(int directionOrder) {
        this.currentDirectionIndex = directionOrder;
    }
}
