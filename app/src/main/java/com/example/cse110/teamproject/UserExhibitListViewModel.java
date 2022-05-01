package com.example.cse110.teamproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserExhibitListViewModel extends AndroidViewModel {
    private LiveData<List<ExhibitNodeItem>> userExhibitListItems;
    private final UserExhibitListItemDao userExhibitListItemDao;

    public UserExhibitListViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        userExhibitListItemDao = db.userExhibitListItemDao();
    }

    public LiveData<List<ExhibitNodeItem>> getExhibitListItems() {
        if (userExhibitListItems == null) {
            loadUsers();
        }
        return userExhibitListItems;
    }

    private void loadUsers() {
        userExhibitListItems = userExhibitListItemDao.getAllLive();
    }

}
