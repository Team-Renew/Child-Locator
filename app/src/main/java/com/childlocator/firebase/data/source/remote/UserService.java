package com.childlocator.firebase.data.source.remote;

import android.app.Application;

import com.childlocator.firebase.data.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    databaseRef.child("users").child(user.getUid()).setValue(user);
    databaseRef.child("usernames").child(user.getUsername()).setValue(user);

  }

  public DatabaseReference getUser(String userUid) {
    return databaseRef.child("users").child(userUid);
  }

  public DatabaseReference getUserByUsername(String username) {
    return databaseRef.child("usernames").child(username);
  }

  public void updateUser(User user) {

  }

  public void deleteUser(String key) {

  }
}