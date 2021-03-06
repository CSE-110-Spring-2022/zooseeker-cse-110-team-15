package com.example.cse110.teamproject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserExhibitListItemDao {

    @Insert
    long insert(UserExhibitListItem userExhibitListItem);

    @Query("SELECT `exhibit_node_items`.* FROM `exhibit_node_items` INNER JOIN `user_exhibit_node_items` " +
            "ON `user_exhibit_node_items`.location_id = `exhibit_node_items`.node_id")
    List<ExhibitNodeItem> getAllUserExhibits();

    @Query("SELECT * FROM `exhibit_node_items` INNER JOIN `user_exhibit_node_items` " +
            "ON `user_exhibit_node_items`.location_id = `exhibit_node_items`.node_id")
    LiveData<List<ExhibitNodeItem>> getAllLive();

    @Query("SELECT COUNT(*) FROM `user_exhibit_node_items`")
    LiveData<Integer> getListSize();

    @Query("DELETE FROM `user_exhibit_node_items`")
    void deleteUserExhibitItems();

    @Query("DELETE FROM `user_exhibit_node_items` WHERE `location_id` = :nodeId")
    void deleteUserExhibitById(String nodeId);
}
