package com.example.cse110.teamproject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExhibitListItemDao {
    @Insert
    long insert(ExhibitNodeItem exhibitNodeItem);

    @Insert
    List<Long> insertAll(List<ExhibitNodeItem> exhibitListItemNode);

    @Query("SELECT * FROM `exhibit_node_items` WHERE `id`=:id")
    ExhibitNodeItem getExhibit(long id);

    @Query("SELECT * FROM `exhibit_node_items` WHERE `kind`='exhibit'")
    List<ExhibitNodeItem> getAllExhibits();


    @Query("SELECT * FROM `exhibit_node_items` WHERE `name` LIKE '%' ||:name || '%'")
    List<ExhibitNodeItem> getExhibits(String name);

    @Query("SELECT * FROM `exhibit_node_items` WHERE `name`=:name")
    ExhibitNodeItem getExhibitByName(String name);


//    @Query("SELECT * FROM `exhibit_node_items` WHERE `name` LIKE :name")
//    List<ExhibitNodeItem> getExhibits(String name);
//    @Update
//    int update(ExhibitNodeItem exhibitNodeItem);

}
