package com.childlocator.firebase.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.childlocator.firebase.data.Constants;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.greenrobot.event.EventBus;

public class LocationService extends Service {
  private LocationManager locationManager;
  private MyLocationListener myLocationListener;
  private Firebase rootDbUrl;
  private Firebase currentUserDbUrl;

  public static double latitude = 42.652;
  public static double longitude = 23.378;

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private static final String LOG_TAG = "LocationService";

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    this.locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    this.myLocationListener = new MyLocationListener();

    // Firebase
    rootDbUrl = new Firebase(Constants.FIREBASE_CHILD_LOCATOR_DB_URL);

    mAuth = FirebaseAuth.getInstance();
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // User is signed in
          Log.d(LOG_TAG, "onAuthStateChanged: signed_in:" + user.getUid());
        } else {
          // User is signed out
          Log.d(LOG_TAG, "onAuthStateChanged: signed_out");
        }

        // ...

        if (user != null) {
          currentUserDbUrl = rootDbUrl.child(Constants.NODE_USERS).child(user.getUid());
        } else {
          currentUserDbUrl = null;
        }
      }
    };

    mAuth.addAuthStateListener(mAuthListener);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }

    /*
     * https://developer.android.com/reference/android/location/LocationManager.html
     *
     * Register for location updates using the named provider, and a LocationListener.
     *
     * requestLocationUpdates(String provider, long minTime, float minDistance, LocationListener listener)     *
     *
     *    provider | String: the name of the provider with which to register
     *     minTime | long: minimum time interval between location updates, in milliseconds
     * minDistance | float: minimum distance between location updates, in meters
     *    listener | LocationListener: a LocationListener whose onLocationChanged(Location) method will be called for each location update
     */
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, myLocationListener);
  }

  @Override
  public void onDestroy() {
    // Firebase
    currentUserDbUrl = null;
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
      mAuthListener = null;
    }
    mAuth = null;

    super.onDestroy();

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }

    locationManager.removeUpdates(myLocationListener);
  }

  public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
      try {
        currentUserDbUrl.child(Constants.NODE_LATITUDE).setValue(location.getLatitude());
        currentUserDbUrl.child(Constants.NODE_LONGITUDE).setValue(location.getLongitude());
      } catch (Exception e) {
      }

      latitude = location.getLatitude();
      longitude = location.getLongitude();
      EventBus.getDefault().post(location);
      
      Log.d(LOG_TAG, "onLocationChanged: latitude: " + latitude);
      Log.d(LOG_TAG, "onLocationChanged: longitude: " + longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
  }
}
