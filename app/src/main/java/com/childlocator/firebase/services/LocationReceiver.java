package com.childlocator.firebase.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.childlocator.firebase.data.Constants;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationReceiver extends BroadcastReceiver {

  private String TAG = this.getClass().getSimpleName();

  private LocationResult mLocationResult;

  private static String strPreviousUserDbUrl = "";
  private static DatabaseReference currentUserDbUrl = null;

  @Override
  public void onReceive(Context context, Intent intent) {
    Bundle bundle = intent.getExtras();
    if (bundle == null) {
      return;
    }

    String strCurrentUserDbUrl = intent.getStringExtra("currentUserDbUrl");

    if (strCurrentUserDbUrl == null || strCurrentUserDbUrl.equals("null")) {
      return;
    }

    if (!strCurrentUserDbUrl.equals(strPreviousUserDbUrl)) {
      strPreviousUserDbUrl = new String(strCurrentUserDbUrl);

      if (strCurrentUserDbUrl.equals("null")) {
        currentUserDbUrl = null;
      } else {
        currentUserDbUrl = FirebaseDatabase.getInstance().getReferenceFromUrl(strCurrentUserDbUrl);
      }
    }

    Location loc = (Location) bundle.get("com.google.android.location.LOCATION");
    if (loc != null) {
      Log.i(TAG, "Location Received: " + String.valueOf(loc.getLatitude()) + " " + String.valueOf(loc.getLongitude()));

      Toast.makeText(
              context,
              "location: " + String.valueOf(loc.getLatitude()) + " " + String.valueOf(loc.getLongitude()),
              Toast.LENGTH_LONG)
              .show();

      if (currentUserDbUrl != null) {
        try {
          currentUserDbUrl.child(Constants.NODE_LATITUDE).setValue(loc.getLatitude());
          currentUserDbUrl.child(Constants.NODE_LONGITUDE).setValue(loc.getLongitude());
        } catch (Exception e) {
        }
      }
    }
  }
}
