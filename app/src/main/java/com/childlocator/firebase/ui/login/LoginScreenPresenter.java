package com.childlocator.firebase.ui.login;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.childlocator.firebase.base.BasePresenter;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.data.source.remote.FirebaseUserService;
import com.childlocator.firebase.data.source.remote.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginScreenPresenter implements BasePresenter {

  private LoginActivity activity;
  private FirebaseUserService firebaseUserService;
  private UserService userService;

  public LoginScreenPresenter(
          LoginActivity activity,
          FirebaseUserService firebaseUserService,
          UserService userService) {
    this.activity = activity;
    this.firebaseUserService = firebaseUserService;
    this.userService = userService;
  }

  @Override
  public void subscribe() {

  }

  @Override
  public void unsubscribe() {

  }

  protected void loginWithEmail(final String email, final String password) {
    activity.showLoading(true);
    firebaseUserService.getUserWithEmail(email, password)
            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  activity.showLoading(false);
                  for (UserInfo profile : task.getResult().getUser().getProviderData()) {
                    String providerId = profile.getProviderId();
                    String uid = profile.getUid();
                    String name = profile.getDisplayName();
                    String email = profile.getEmail();
                    Uri photoUri = profile.getPhotoUrl();
                    Log.d("childlocator", providerId + " " + uid + " " + name + " " + email + " " + photoUri);
                  }
                  processLogin(task.getResult().getUser(), task.getResult().getUser().getProviderData().get(1));
                } else {
                  activity.showLoading(false);
                  createAccount(email, password);
                }
              }
            });

  }

  private void processLogin(FirebaseUser firebaseUser, UserInfo userInfo) {
    final User user = User.newInstance(firebaseUser, userInfo);
    userService.getUser(user.getUid()).addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                User remoteUser = dataSnapshot.getValue(User.class);
                if (remoteUser == null || remoteUser.getUsername() == null) {
                  activity.showInsertUsername(user);
                } else {
                  activity.showLoginSuccess(remoteUser);
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                activity.showLoginFail();
              }
            }
    );
  }

  protected void createAccount(String email, String password) {
    activity.showLoading(true);
    firebaseUserService.createUserWithEmail(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  processLogin(task.getResult().getUser(), task.getResult().getUser().getProviderData().get(1));
                } else {
                  activity.showLoading(false);
                  activity.showLoginFail();
                }
              }
            });
  }

  public void createUser(final User user, final String username) {
    activity.showLoading(true);
    userService.getUserByUsername(username).addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();
                if (!exists) {
                  activity.showLoading(false);
                  user.setUsername(username);
                  userService.createUser(user);
                  activity.showLoginSuccess(user);
                } else {
                  activity.showLoading(false);
                  activity.showExistUsername(user, username);
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                activity.showLoading(false);
                activity.showInsertUsername(user);
              }
            }
    );
  }
}
