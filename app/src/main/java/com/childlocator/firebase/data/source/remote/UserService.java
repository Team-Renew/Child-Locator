package com.childlocator.firebase.data.source.remote;

import android.app.Application;

import com.childlocator.firebase.data.Constants;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.services.BackgroundLocationService2;
//import com.childlocator.firebase.services.LocationService;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class UserService {
  private Application application;
  private DatabaseReference databaseRef;

  public UserService(Application application) {
    this.application = application;
    this.databaseRef = FirebaseDatabase.getInstance().getReference();
  }

  public void createUser(User user) {
    if (user.getPhoto_url() == null) {
      user.setPhoto_url("NOT");
    }

    user.setConnection(Constants.KEY_ONLINE);
    user.setCreatedAt(String.valueOf(new Date().getTime()));

    double lat = 42.652;
    double lng = 23.378;
    if (BackgroundLocationService2.latitude != 0) {
      lat = BackgroundLocationService2.latitude;
    }
    if (BackgroundLocationService2.longitude != 0) {
      lng = BackgroundLocationService2.longitude;
    }
    user.setLatitude(lat);
    user.setLongitude(lng);

    databaseRef.child(Constants.NODE_USERS).child(user.getUid()).setValue(user);
  }

  public DatabaseReference getUser(String userUid) {
    return databaseRef.child("cl_users").child(userUid);
  }

  public void updateUser(User user) {
  }

  public void deleteUser(String key) {
  }
}
