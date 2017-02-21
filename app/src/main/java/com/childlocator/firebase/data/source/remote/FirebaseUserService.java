package com.childlocator.firebase.data.source.remote;

import android.app.Application;

import com.childlocator.firebase.data.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseUserService {
  private Application application;
  private FirebaseAuth firebaseAuth;

  public FirebaseUserService(Application application) {
    this.application = application;
    this.firebaseAuth = FirebaseAuth.getInstance();
  }

  public Task<AuthResult> getUserWithEmail(String email, String password) {
    return firebaseAuth.signInWithEmailAndPassword(email, password);
  }

  public Task<AuthResult> createUserWithEmail(String email, String password) {
    return firebaseAuth.createUserWithEmailAndPassword(email, password);
  }

  public void logOut(String provider) {
    firebaseAuth.signOut();
  }

  public void deleteUser(String uid) {

  }

  public void updateUser(User user) {

  }
}
