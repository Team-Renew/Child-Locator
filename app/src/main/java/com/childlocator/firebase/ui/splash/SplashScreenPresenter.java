package com.childlocator.firebase.ui.splash;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.childlocator.firebase.base.BasePresenter;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.data.source.remote.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenPresenter implements BasePresenter, SplashScreenContract.Presenter {

  // Splash screen timer
  private static int SPLASH_TIME_OUT = 4000;

  private SplashActivity activity;
  private UserService userService;
  private FirebaseAuth firebaseAuth;
  private FirebaseAuth.AuthStateListener authListener;

  public SplashScreenPresenter(SplashActivity activity, UserService userService) {
    this.activity = activity;
    this.userService = userService;
    this.firebaseAuth = FirebaseAuth.getInstance();
  }

  @Override
  public void subscribe() {
    this.authListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
          /*
           * Showing splash screen with a timer. This will be useful when you
           * want to show your app logo / company
           */

          @Override
          public void run() {
            // This code will be executed once the timer is over

            if (user == null) {
              activity.showLoginActivity();
            } else {
              processLogin(user);
            }
          }
        }, SPLASH_TIME_OUT);
      }
    };

    this.firebaseAuth.addAuthStateListener(this.authListener);
  }

  @Override
  public void unsubscribe() {
    if (this.authListener != null) {
      this.firebaseAuth.removeAuthStateListener(this.authListener);
    }
  }

  private void processLogin(FirebaseUser user) {
    this.userService.getUser(user.getUid()).addListenerForSingleValueEvent(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null || user.getEmail() == null) {
                  activity.showLoginActivity();
                } else {
                  activity.showChildrenActivity(user);
                }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {
                activity.showLoginActivity();
              }
            }
    );
  }
}
