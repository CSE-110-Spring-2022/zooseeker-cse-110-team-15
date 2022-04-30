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

@Entity(tableName = "exhibit_node_items")
public class ExhibitNodeItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @Expose
    @NonNull
    public String kind;

    @Expose
    @NonNull
    public String name;
    // public String tags;

    ExhibitNodeItem(@NonNull String kind, @NonNull String name) {
        this.kind = kind;
        this.name = name;
        // this.tags = tags;
    }

    @Override
    public String toString() {
        return "ExhibitListItem{" +
                "id=" + id +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                // ", tags=" + tags +
                '}';
    }

    public static List<ExhibitNodeItem> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            //Gson gson = new Gson();
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation().create();
            Type type = new TypeToken<List<ExhibitNodeItem>>(){}.getType();
            return gson.fromJson(reader, type);

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
