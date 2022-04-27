package com.example.cse110.teamproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {MockExhibitListItem.class}, version = 1)
public abstract class ExhibitDatabase extends RoomDatabase {
    public abstract ExhibitListItemDao exhibitListItemDao();
}
