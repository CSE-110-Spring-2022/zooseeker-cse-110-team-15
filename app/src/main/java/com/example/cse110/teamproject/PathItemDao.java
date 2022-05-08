package com.example.cse110.teamproject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PathItemDao {

    @Insert
    long insert(PathItem pathItem);

    @Query("SELECT * FROM `path_items` WHERE `order`=:order")
    PathItem getByOrder(int order);

    @Query("SELECT * FROM `path_items`")
    List<PathItem> getAll();

    @Query("DELETE FROM `path_items`")
    void deletePathItems();

}
