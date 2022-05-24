package com.example.cse110.teamproject;

import android.location.Location;

import androidx.activity.ComponentActivity;

import java.util.ArrayList;
import java.util.List;

public class MockUserLocation{

    Location currentLocation;

    MockUserLocation(ComponentActivity context, Location location) {
        this.currentLocation = location;
    }

    public void setUserLocation(Location location) {
        this.currentLocation = location;
        notifyLocationChange();
    }

    public Location getUserLocation() {
        return currentLocation;
    }

    List<LocationObserver> observers = new ArrayList<>();

    public void notifyLocationChange() {
        for (LocationObserver o: observers) {
            o.updateLocation(currentLocation);
        }
    }

    public void addLocationChangedObservers(LocationObserver o) {
        observers.add(o);
    }

}
