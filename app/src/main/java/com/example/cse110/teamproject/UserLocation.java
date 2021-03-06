package com.example.cse110.teamproject;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

//import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class UserLocation {

    ComponentActivity context;

    private final PermissionChecker permissionChecker;

    private boolean mocked;

    private Location lastVisitedLocation;
    private Location currentLocation;
    List<LocationObserver> observers;
    @RequiresApi(api = Build.VERSION_CODES.S)
    UserLocation(ComponentActivity context) {

        currentLocation = new Location("");
        currentLocation.setLatitude(0);
        currentLocation.setLongitude(0);

        mocked = true;

        this.context = context;
        permissionChecker = new PermissionChecker(this.context);
        observers = new ArrayList<>();

        /* Permissions Setup */
        if (permissionChecker.ensurePermissions()) return;

        /* Listen for Location Updates */
        {

            var provider = LocationManager.FUSED_PROVIDER;
            var locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    if(mocked) { return; }
                    Log.d("LAB7", String.format("Location changed: %s", location));
                    lastVisitedLocation = currentLocation;
                    currentLocation = location;
                    notifyLocationChange();
                }
            };

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
        }

    }

    public Location getUserLocation() {
        return currentLocation;
    }

    public boolean ensurePermissions() {
        return permissionChecker.ensurePermissions();
    }


    public void notifyLocationChange() {
        Log.d("<userLocationObservers>", observers.toString());
        for (LocationObserver o: observers) {
            Log.d("<notifyLocationChange>", o + " notified");
            o.updateLocation(currentLocation);
        }
    }

    public void addLocationChangedObservers(LocationObserver o) {
        observers.add(o);
    }

    public void setMocked(boolean mocked) {
        this.mocked = mocked;
    }

    public boolean isMocked() {
        return this.mocked;
    }

    public void setCurrentLocation(float longitude, float latitude) {
        currentLocation.setLongitude(longitude);
        currentLocation.setLatitude(latitude);
        Log.d("<userLocation>", "user location set to (" + longitude + ", " + latitude + ").");
        notifyLocationChange();
    }

}