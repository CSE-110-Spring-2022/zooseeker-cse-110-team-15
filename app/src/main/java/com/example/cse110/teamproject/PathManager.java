package com.example.cse110.teamproject;

import android.content.Context;
import android.location.Location;
import android.util.Pair;

import org.jgrapht.GraphPath;

import java.util.List;

// stores path and changes it
public class PathManager implements LocationObserver{

    List<GraphPath<String, IdentifiedWeightedEdge>> paths;
    Location currentLocation;
    private int currentDirectionIndex;

    ExhibitListItemDao exhibitListItemDao;

    PathManager(Context context) {
        exhibitListItemDao = ExhibitDatabase.getSingleton(context)
                .exhibitListItemDao();
        paths = PathFinder.findPath(context);
    }

    public String currentVertexLocation(Location location) {
        // get user location
        // get distance to all points

        Pair<String, Float> closest = new Pair<String, Float>("", Float.POSITIVE_INFINITY);

        List<ExhibitNodeItem> exhibits = exhibitListItemDao.getAllExhibits();
        for (ExhibitNodeItem exhibitNodeItem : exhibits) {
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
        if (currentPathVertices.indexOf(currentVertexLocation(currentLocation)) == -1) {
            notifyUserOffTrack(currentPath);
            return true;
        }
        return false;
        // logic for determining if user is on or off track; calls userOnTrack if necessary
        // if currentLocation corres. to some vertex in GraphPath do nothing
        // if currentLocation corres. to no vertices in GraphPath call notifyOffTrack()
    }

    public void notifyOffTrackWithDiffNext() {

    }

    List<PathChangeObserver> pathChangeObservers;

    //
    public void notifyUserOffTrack(GraphPath<String, IdentifiedWeightedEdge> currentPath) {
        // TODO
        // notifyOnTrackObservers() (for US3 Notify and Replan)
        for (PathChangeObserver o : pathChangeObservers) {
            o.update(currentPath);
        }

    }

    public void update(Location currentLocation) {
        this.currentLocation = currentLocation;
        /* ... */
    }


    public void updateCurrentDirectionIndex(int directionOrder) {
        this.currentDirectionIndex = directionOrder;
    }
}
