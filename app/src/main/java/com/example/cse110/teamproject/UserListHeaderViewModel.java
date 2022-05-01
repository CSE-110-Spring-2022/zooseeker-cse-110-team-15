package com.example.cse110.teamproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserListHeaderViewModel extends AndroidViewModel {
    private LiveData<Integer> userExhibitListItems;
    private final UserExhibitListItemDao userExhibitListItemDao;

    public UserListHeaderViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        userExhibitListItemDao = db.userExhibitListItemDao();
    }

    public LiveData<Integer> getListSize() {
        if (userExhibitListItems == null) {
            loadSize();
        }
        return userExhibitListItems;
    }

    private void loadSize() {
        userExhibitListItems = userExhibitListItemDao.getListSize();
    }
}
