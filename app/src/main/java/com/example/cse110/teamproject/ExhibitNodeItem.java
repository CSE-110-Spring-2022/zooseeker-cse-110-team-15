package com.example.cse110.teamproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "exhibit_node_items")
public class ExhibitNodeItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @Expose
    @NonNull
    @SerializedName(value = "node_id", alternate = "id")
    public String node_id;

    @Expose
    @NonNull
    public String kind;

    @Expose
    @NonNull
    public String name;

    @Expose
    @NonNull
    public float lat;

    @Expose
    @NonNull
    public float lng;

    ExhibitNodeItem(@NonNull String kind, @NonNull String name, @NonNull String node_id, @NonNull float lat, @NonNull float lng) {
        this.kind = kind;
        this.name = name;
        this.node_id = node_id;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExhibitNodeItem that = (ExhibitNodeItem) o;
        return id == that.id && lat == that.lat && lng == that.lng && node_id.equals(that.node_id) && kind.equals(that.kind) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, node_id, kind, name);
    }

    @Override
    public String toString() {
        return "ExhibitNodeItem{" +
                "id=" + id +
                ", node_id='" + node_id + '\'' +
                ", kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }

    public static List<ExhibitNodeItem> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
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
