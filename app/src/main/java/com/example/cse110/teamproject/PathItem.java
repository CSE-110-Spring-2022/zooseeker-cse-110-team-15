package com.example.cse110.teamproject;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "path_items")
public class PathItem {

    @PrimaryKey(autoGenerate = true)
    long id;

    @Expose
    @NonNull
    @SerializedName(value = "node_id", alternate = "id")
    public String node_id;

    // array of edges (curr, target) serialized in String format, for the current direction objective
    @Expose
    @NonNull
    public List<String> curr_directions;

    @Expose
    @NonNull
    int order;

    public PathItem(@NonNull String node_id, @NonNull List<String> curr_directions, int order) {
        this.node_id = node_id;
        this.curr_directions = curr_directions;
        this.order = order;
    }

    @Override
    public String toString() {
        return "PathItem{" +
                "id=" + id +
                ", node_id='" + node_id + '\'' +
                ", curr_directions=" + curr_directions +
                ", order=" + order +
                '}';
    }
}
