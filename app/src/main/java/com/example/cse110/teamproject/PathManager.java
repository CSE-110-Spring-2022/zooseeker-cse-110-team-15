package com.example.cse110.teamproject;

import android.content.Context;
import android.location.Location;

import org.jgrapht.GraphPath;

import java.util.List;

// stores path and changes it
public class PathManager implements LocationObserver{

    List<GraphPath<String, IdentifiedWeightedEdge>> paths;
    Location currentLocation;
    private int currentDirectionIndex;

    PathManager(Context context) {
        paths = PathFinder.findPath(context);
    }

    public String currentVertexLocation(Location location) {
        return "trash value"; // FIXME: implement this method (given that location format is known)
    }

    // off track is determined in relation to the current directions page the user is on.
    public boolean userOffTrack(Location currentLocation) {
        GraphPath<String,IdentifiedWeightedEdge> currentPath = paths.get(currentDirectionIndex);
        List<String> currentPathVertices = currentPath.getVertexList();
        if (currentPathVertices.indexOf(currentVertexLocation(currentLocation)) == -1) {
            notifyCurrPathChange(currentPath);
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
    public void notifyCurrPathChange(GraphPath<String, IdentifiedWeightedEdge> currentPath) {
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
