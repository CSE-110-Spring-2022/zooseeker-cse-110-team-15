package com.example.cse110.teamproject;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;


public class UserLocation {

    ComponentActivity context;

    private final PermissionChecker permissionChecker = new PermissionChecker(context);

    private Location lastVisitedLocation;
    private Location currentLocation;

    UserLocation(ComponentActivity context) {

        this.context = context;

        /* Permissions Setup */
        if (permissionChecker.ensurePermissions()) return;

        /* Listen for Location Updates */
        {
            var provider = LocationManager.GPS_PROVIDER;
            var locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            var locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
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

    private boolean ensurePermissions() {
        return permissionChecker.ensurePermissions();
    }

    List<LocationObserver> observers;

    public void notifyLocationChange() {
        for (LocationObserver o: observers) {
            o.updateLocation(currentLocation);
        }
    }
}