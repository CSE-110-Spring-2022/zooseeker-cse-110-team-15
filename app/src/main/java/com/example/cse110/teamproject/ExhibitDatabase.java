package com.example.cse110.teamproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;


    @Database(entities = {ExhibitNodeItem.class, UserExhibitListItem.class}, version = 1)
    public abstract class ExhibitDatabase extends RoomDatabase {
        static ExhibitDatabase singleton = null;

        public abstract ExhibitListItemDao exhibitListItemDao();
        public abstract UserExhibitListItemDao userExhibitListItemDao();


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
                            Executors.newSingleThreadScheduledExecutor().execute(() -> {
                                getSingleton(context).userExhibitListItemDao();
                            });
                        }
                    })
                    .build();
        }

        public static void resetSingleton() {
            singleton = null;
        }

    }

