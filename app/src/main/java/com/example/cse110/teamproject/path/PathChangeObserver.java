package com.example.cse110.teamproject.path;

import com.example.cse110.teamproject.IdentifiedWeightedEdge;

import org.jgrapht.GraphPath;

import java.util.List;

public interface PathChangeObserver {

    public void update(List<GraphPath<String, IdentifiedWeightedEdge>> path);

}
