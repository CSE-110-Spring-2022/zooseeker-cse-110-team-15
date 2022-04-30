package com.example.cse110.teamproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.ToDoubleBiFunction;

@Database(entities = {ExhibitNodeItem.class}, version = 1)
public abstract class ExhibitDatabase extends RoomDatabase {
    private static ExhibitDatabase singleton = null;

    public abstract ExhibitListItemDao exhibitListItemDao();

    public synchronized static ExhibitDatabase getSingleton(Context context) {
        if (singleton == null) {
            singleton = ExhibitDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static ExhibitDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, ExhibitDatabase.class, "exhibit_nodes.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<ExhibitNodeItem> nodes = ExhibitNodeItem
                                    .loadJSON(context, "sample_node_info.json");
                            getSingleton(context).exhibitListItemDao().insertAll(nodes);
                        });
                    }
                })
                .build();
    }
}

