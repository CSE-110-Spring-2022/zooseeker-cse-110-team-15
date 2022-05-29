package com.example.cse110.teamproject.path;

import com.example.cse110.teamproject.IdentifiedWeightedEdge;

import org.jgrapht.GraphPath;

public class PathInfo {
    public enum Direction {
        FORWARDS, REVERSE
    }
    protected GraphPath<String, IdentifiedWeightedEdge> path;
    protected Direction direction = Direction.FORWARDS;
    PathInfo(GraphPath<String, IdentifiedWeightedEdge> path) {
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
