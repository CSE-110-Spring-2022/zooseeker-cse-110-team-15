package com.example.cse110.teamproject.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.ListIterator;

public class MockLocation {
    private FusedLocationProviderClient mFusedLocationClient;

    public MockLocation(Context context) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.setMockMode(true);
    }

    public static Location makeLocation (LatLng latLng) {
        Location loc = new Location("");
        loc.setAltitude(100);
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        loc.setTime(System.currentTimeMillis());
        loc.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        loc.setAccuracy(6f);

        return loc;
    }

    public void setCurrLocation(Location loc) {
        mFusedLocationClient.setMockLocation(loc);
        mFusedLocationClient.flushLocations();
        Log.d("mocked_location", "mocked location" + loc.toString());
    }
    public void setCurrLocation(LatLng latLng) {
        setCurrLocation(this.makeLocation(latLng));
    }
}
