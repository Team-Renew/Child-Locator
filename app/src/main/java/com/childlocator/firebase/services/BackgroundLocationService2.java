package com.childlocator.firebase.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.childlocator.firebase.data.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class BackgroundLocationService2 extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
  public static final String CURRENT_USER_DB_URL = "currentUserDbUrl";

  private LocationRequest mLocationRequest;
  private GoogleApiClient mGoogleApiClient;

  private DatabaseReference rootUrl;
  private DatabaseReference urlCurrentUser;
  public static double latitude = 0;
  public static double longitude = 0;

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private static final String LOG_TAG = "BackgroundLocationSrvc2";

  private Intent locationIntent;
  private PendingIntent pendingIntent;

  @Override
  public void onCreate() {
    super.onCreate();

    locationIntent = new Intent(BackgroundLocationService2.this, LocationReceiver.class);

    /*** start Firebase code ***/
    //Firebase.setAndroidContext(this);

    rootUrl = FirebaseDatabase.getInstance().getReference();

    mAuth = FirebaseAuth.getInstance();
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (locationIntent.hasExtra(CURRENT_USER_DB_URL)) {
          locationIntent.removeExtra(CURRENT_USER_DB_URL);
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          urlCurrentUser = rootUrl.child(Constants.NODE_USERS).child(user.getUid());

          // User is signed in
          Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());

          locationIntent.putExtra(CURRENT_USER_DB_URL, urlCurrentUser.toString());
        } else {
          urlCurrentUser = null;

          // User is signed out
          Log.d(LOG_TAG, "onAuthStateChanged:signed_out");

          locationIntent.putExtra(CURRENT_USER_DB_URL, "null");
        }

        // ...

        if (user != null) {
          if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.reconnect();
          } else {
            mGoogleApiClient.connect();
          }
        } else {
          if (mGoogleApiClient.isConnected()) {
            stopLocationUpdate();
            mGoogleApiClient.disconnect();
          }
        }
      }
    };

    mAuth.addAuthStateListener(mAuthListener);
    /*** end Firebase code ***/

    buildGoogleApiClient();
    Log.i(LOG_TAG, "onCreate");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.i(LOG_TAG, "onStartCommand");

    if (!mGoogleApiClient.isConnected())
      mGoogleApiClient.connect();
    return START_STICKY;
  }


  @Override
  public void onConnected(Bundle bundle) {
    Log.i(LOG_TAG, "onConnected" + bundle);

    startLocationUpdate();
  }

  @Override
  public void onConnectionSuspended(int i) {
    Toast.makeText(
            this,
            DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.",
            Toast.LENGTH_SHORT)
            .show();

    Log.i(LOG_TAG, "onConnectionSuspended " + i);

    stopLocationUpdate();
  }

  @Override
  public void onDestroy() {

    // Firebase
    urlCurrentUser = null;
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
      mAuthListener = null;
    }
    mAuth = null;

    stopLocationUpdate();

    super.onDestroy();
    Log.i(LOG_TAG, "onDestroy");
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
    Log.i(LOG_TAG, "onConnectionFailed ");
  }

  private void initLocationRequest() {
    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(30000);        // 30 seconds
    mLocationRequest.setFastestInterval(30000); // 30 seconds
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private void startLocationUpdate() {
    initLocationRequest();

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }

    this.pendingIntent = PendingIntent
            .getBroadcast(this, 54321, locationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
            mLocationRequest, this.pendingIntent); // This is the changed line.
  }

  private void stopLocationUpdate() {
    //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this.pendingIntent);
  }

  protected synchronized void buildGoogleApiClient() {
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addOnConnectionFailedListener(this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build();
  }
}
