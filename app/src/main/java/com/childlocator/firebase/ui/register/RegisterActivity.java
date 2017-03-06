package com.childlocator.firebase.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.childlocator.firebase.R;
import com.childlocator.firebase.data.Constants;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.services.BackgroundLocationService2;
import com.childlocator.firebase.ui.children.ChildrenActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class RegisterActivity extends AppCompatActivity {
  @Bind(R.id.etName)
  EditText edtName;
  @Bind(R.id.etEmail)
  EditText edtEmail;
  @Bind(R.id.etPw)
  EditText edtPass;
  @Bind(R.id.btnRegister)
  Button btnRegister;

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private static final String LOG_TAG = "RegisterActivity";

  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    ButterKnife.bind(this);

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
        if (user != null) {
          final DatabaseReference rootUrl = FirebaseDatabase.getInstance().getReference();
          final String username = edtName.getText().toString();
          final String email = user.getEmail();
          final String provider = user.getProviderId();
          final String photo_url = "NOT";
          long createTime = new Date().getTime();
          double lat = 42.652;
          double lng = 23.378;
          if (BackgroundLocationService2.latitude != 0) {
            lat = BackgroundLocationService2.latitude;
          }
          if (BackgroundLocationService2.longitude != 0) {
            lng = BackgroundLocationService2.longitude;
          }
          Log.d(LOG_TAG, "onAuthenticated: latitude: " + lat);
          Log.d(LOG_TAG, "onAuthenticated: longitude: " + lng);
          String uid = user.getUid();
          rootUrl.child(Constants.NODE_USERS)
                  .child(uid)
                  .setValue(new User(
                                  uid,
                                  username,
                                  email,
                                  provider,
                                  photo_url,
                                  username,
                                  Constants.KEY_ONLINE,
                                  String.valueOf(createTime),
                                  lat,
                                  lng),
                          new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                              EventBus.getDefault().post(Constants.KEY_CLOSE);
                              startActivity(new Intent(RegisterActivity.this, ChildrenActivity.class));
                              finish();
                            }
                          });
        }
      }
    };
  }

  @OnClick(R.id.btnRegister)
  public void setBtnRegister() {
    final String name = edtName.getText().toString();
    final String email = edtEmail.getText().toString();
    final String password = edtPass.getText().toString();
    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {

    } else {
      mAuth.createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                  Log.d(LOG_TAG, "createUserWithEmailAndPassword: onComplete:" + task.isSuccessful());

                  // If sign in fails, display a message to the user. If sign in succeeds
                  // the auth state listener will be notified and logic to handle the
                  // signed in user can be handled in the listener.
                  if (!task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                  }

                  // ...
                }
              });
    }
  }
}
