package com.example.cse110.teamproject.path;

import com.example.cse110.teamproject.IdentifiedWeightedEdge;

import org.jgrapht.GraphPath;

public class PathInfo {
    protected GraphPath<String, IdentifiedWeightedEdge> path;
    public String nodeId;

    PathInfo(String nodeId, GraphPath<String, IdentifiedWeightedEdge> path) {
        this.nodeId = nodeId;
        this.path = path;
    }

    public GraphPath<String, IdentifiedWeightedEdge> getPath() {
        return this.path;
    }
}
