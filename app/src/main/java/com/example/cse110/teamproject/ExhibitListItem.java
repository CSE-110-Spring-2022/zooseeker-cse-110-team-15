package com.example.cse110.teamproject;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExhibitListItem {
    public String text;
    public int order;

    ExhibitListItem(String text, int order) {
        this.text = text;
        this.order = order;
    }

    @Override
    public String toString() {
        return "ExhibitListItem{" +
                "exhibit='" + text + '\'' +
                ", order=" + order +
                '}';
    }

    public static List<ExhibitListItem> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ExhibitListItem>>(){}.getType();
            return gson.fromJson(reader, type);

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
