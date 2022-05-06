package com.example.cse110.teamproject;

import androidx.room.TypeConverter;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        ArrayList<String> array = new ArrayList<String>(Arrays.asList(value.split(",")));
        return array;
    }

    @TypeConverter
    public static String toString(List<String> array) {
        return String.join(",", array);
    }

}
