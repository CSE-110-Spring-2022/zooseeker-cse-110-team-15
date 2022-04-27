package com.example.cse110.teamproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ExhibitListItemDao {
    @Insert
    long insert(MockExhibitListItem exhibitListItem);

    @Query("SELECT * FROM `user_exhibits_list` WHERE `id`=:id")
    MockExhibitListItem get(long id);

    @Update
    int update(MockExhibitListItem exhibitListItem);

}
