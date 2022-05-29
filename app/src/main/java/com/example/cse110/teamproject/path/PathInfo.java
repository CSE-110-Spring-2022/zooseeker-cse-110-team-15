package com.example.cse110.teamproject.path;

import com.example.cse110.teamproject.IdentifiedWeightedEdge;

import org.jgrapht.GraphPath;

public class PathInfo {
    public String nodeId;
    private Direction direction = Direction.FORWARDS;

    public enum Direction {
        FORWARDS, REVERSE
    }
    protected GraphPath<String, IdentifiedWeightedEdge> path;
    PathInfo(String nodeId, GraphPath<String, IdentifiedWeightedEdge> path) {
        this.nodeId = nodeId;
        this.path = path;
    }

    public GraphPath<String, IdentifiedWeightedEdge> getPath() {
        return this.path;
    }

    protected void setPath(GraphPath<String, IdentifiedWeightedEdge> path) {
        this.path = path;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
