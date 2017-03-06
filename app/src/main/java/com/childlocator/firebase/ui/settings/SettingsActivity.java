package com.childlocator.firebase.ui.settings;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.childlocator.firebase.R;
import com.childlocator.firebase.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

  @Bind(R.id.btnLogout)
  Button btnLogout;

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private static final String LOG_TAG = "SettingsActivity";

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    mAuth = FirebaseAuth.getInstance();
    mAuthListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // User is signed in
          Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
        } else {
          // User is signed out
          Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
        }

        // ...

        if (user == null) {
          startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
          finish();
        }
      }
    };
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @OnClick(R.id.btnLogout)
  public void btnLogout() {
    if (mAuth != null) {
      mAuth.signOut();
    }
  }
}
