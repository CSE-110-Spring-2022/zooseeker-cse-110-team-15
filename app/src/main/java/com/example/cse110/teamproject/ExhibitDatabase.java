package com.example.cse110.teamproject;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;


    @Database(entities = {ExhibitNodeItem.class, UserExhibitListItem.class, PathItem.class}, version = 1)
    @TypeConverters({Converters.class})
    public abstract class ExhibitDatabase extends RoomDatabase {
        private static ExhibitDatabase singleton = null;

        public abstract ExhibitListItemDao exhibitListItemDao();
        public abstract UserExhibitListItemDao userExhibitListItemDao();
        public abstract PathItemDao pathItemDao();

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
                                        .loadJSON(context, "zoo_node_info.json");
                                getSingleton(context).exhibitListItemDao().insertAll(nodes);
                            });
                            Executors.newSingleThreadScheduledExecutor().execute(() -> {
                                getSingleton(context).userExhibitListItemDao();
                            });
                            Executors.newSingleThreadScheduledExecutor().execute(() -> {
                                getSingleton(context).pathItemDao();
                            });
                        }
                    })
                    .build();
        }

        public static void resetSingleton() {
            singleton = null;
        }

        @VisibleForTesting
        public static void injectTestDatabase(ExhibitDatabase testDatabase) {
            if (singleton != null) {
                singleton.close();
            }
            singleton = testDatabase;
        }

    }

