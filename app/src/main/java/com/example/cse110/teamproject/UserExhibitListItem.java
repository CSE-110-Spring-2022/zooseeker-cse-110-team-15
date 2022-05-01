package com.example.cse110.teamproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

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
