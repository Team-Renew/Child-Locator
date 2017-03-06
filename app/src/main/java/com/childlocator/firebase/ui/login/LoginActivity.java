package com.childlocator.firebase.ui.login;

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
import com.childlocator.firebase.ui.children.ChildrenActivity;
import com.childlocator.firebase.ui.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class LoginActivity extends AppCompatActivity {
  @Bind(R.id.etEmail)
  EditText etEmail;
  @Bind(R.id.etPw)
  EditText etPw;
  @Bind(R.id.btnLogin)
  Button btnLogin;
  @Bind(R.id.btnRegister)
  Button btnRegister;

  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthListener;
  private static final String LOG_TAG = "LoginActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
    EventBus.getDefault().register(this);
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
          startActivity(new Intent(LoginActivity.this, ChildrenActivity.class));
          finish();
        }
      }
    };
  }

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
  protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
  }

  public void onEvent(String event) {
    if (event.equals(Constants.KEY_CLOSE)) {
      LoginActivity.this.finish();
    }
  }

  @OnClick(R.id.btnLogin)
  public void btnLogin() {
    String email = etEmail.getText().toString();
    String password = etPw.getText().toString();
    if (email.isEmpty() || password.isEmpty()) {

    } else {
      mAuth.signInWithEmailAndPassword(email, password)
              .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                  Log.d(LOG_TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                  // If sign in fails, display a message to the user. If sign in succeeds
                  // the auth state listener will be notified and logic to handle the
                  // signed in user can be handled in the listener.
                  if (!task.isSuccessful()) {
                    Log.w(LOG_TAG, "signInWithEmail", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT)
                            .show();
                  }

                  // ...
                }
              });
    }
  }

  @OnClick(R.id.btnRegister)
  public void setBtnRegister() {
    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
  }
}
