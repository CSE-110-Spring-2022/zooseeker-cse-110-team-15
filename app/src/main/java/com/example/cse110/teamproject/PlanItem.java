package com.example.cse110.teamproject;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class PlanItem {
    public int distance;
    public String exhibit;
    public String street;

    PlanItem(String street, String exhibit, int distance){
        this.distance = distance;
        this.street = street;
        this.exhibit = exhibit;
    }

    public static List<PlanItem> loadJSON(Context context, String path){
        try{
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<PlanItem>>(){}.getType();
            return gson.fromJson(reader,type);

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public String toString(){
        return "PlanItem{" +
                "distance='" + distance + '\'' +
                ", exhibit=" + exhibit +
                ", street=" + street +
                '}';
    }
}
