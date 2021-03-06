package com.example.cse110.teamproject;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

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

    @Query("SELECT * FROM `exhibit_node_items`")
    List<ExhibitNodeItem> getAllNodes();

    @Query("SELECT * FROM `exhibit_node_items` WHERE `name` LIKE '%' ||:name || '%'"
            + "AND `kind`='exhibit'")
    List<ExhibitNodeItem> getExhibits(String name);


    @Query("SELECT * FROM `exhibit_node_items` WHERE `name`=:name")
    ExhibitNodeItem getExhibitByName(String name);

    @Query("SELECT * FROM `exhibit_node_items` WHERE `node_id`=:node_id")
    ExhibitNodeItem getExhibitByNodeId(String node_id);

    @Query("DELETE FROM `exhibit_node_items`")
    void deleteExhibitItems();

}
