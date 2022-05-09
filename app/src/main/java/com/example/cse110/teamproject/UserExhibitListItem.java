package com.example.cse110.teamproject;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;

@Entity(tableName = "user_exhibit_node_items")
public class UserExhibitListItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @Expose
    @NonNull
    public String location_id;

    public UserExhibitListItem(@NonNull String location_id) {
        this.location_id = location_id;
    }

    @Override
    public String toString() {
        return "UserExhibitListItem{" +
                "id=" + id +
                '}';
    }

}
