package com.childlocator.firebase.data.source.remote;

import android.app.Application;

import com.childlocator.firebase.LocationService;
import com.childlocator.firebase.data.Constants;
import com.childlocator.firebase.data.model.User;
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

    double lat = 0;
    double lng = 0;
    if (LocationService.latitude != 0) {
      lat = LocationService.latitude;
    }
    if (LocationService.longitude != 0) {
      lng = LocationService.longitude;
    }
    user.setLatitude(lat);
    user.setLongitude(lng);

    databaseRef.child("cl_users").child(user.getUid()).setValue(user);
    databaseRef.child("usernames").child(user.getUsername()).setValue(user);
  }

  public DatabaseReference getUser(String userUid) {
    return databaseRef.child("cl_users").child(userUid);
  }

  public DatabaseReference getUserByUsername(String username) {
    return databaseRef.child("usernames").child(username);
  }

  public void updateUser(User user) {

  }

  public void deleteUser(String key) {

  }
}
