package com.example.cse110.teamproject;

import org.jgrapht.GraphPath;

public interface PathChangeObserver {

    public void update(GraphPath<String,IdentifiedWeightedEdge> path);

}
