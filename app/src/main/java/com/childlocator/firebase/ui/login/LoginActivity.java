package com.childlocator.firebase.ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.childlocator.firebase.App;
import com.childlocator.firebase.R;
import com.childlocator.firebase.base.BaseActivity;
import com.childlocator.firebase.data.model.User;
import com.childlocator.firebase.ui.map.MapActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

  @Bind(R.id.etEmail)
  EditText etEmail;
  @Bind(R.id.etPw)
  EditText etPw;
  @Bind(R.id.btnLogin)
  Button btnLogin;
  @Bind(R.id.pbLoading)
  ProgressBar pbLoading;

  @Inject
  LoginScreenPresenter presenter;
  @Inject
  AlertDialog.Builder addAlertDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    presenter.subscribe();
  }

  @Override
  protected void onStop() {
    super.onStop();
    presenter.unsubscribe();
  }

  @Override
  protected void setupActivityComponent() {
    App.get(this).getAppComponent()
            .plus(new LoginScreenModule(this))
            .inject(this);
  }

  @OnClick(R.id.btnLogin)
  public void onBtnLogin() {
    String email = etEmail.getText().toString();
    String password = etPw.getText().toString();

    presenter.loginWithEmail(email, password);
  }

  public void showLoginFail() {
    Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
  }

  public void showLoginSuccess(User user) {
    MapActivity.startWithUser(this, user);
  }

  public void showLoading(boolean loading) {
    pbLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
  }

  public void showInsertUsername(final User user) {

    addAlertDialog.setTitle("Insert your username");
    addAlertDialog.setMessage("Be sure to enter");

    final EditText etUsername = new EditText(this);
    etUsername.setSingleLine();
    addAlertDialog.setView(etUsername);

    addAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
        String username = etUsername.getText().toString();
        dialog.dismiss();
        presenter.createUser(user, username);
      }
    });

    addAlertDialog.show();
  }

  public void showExistUsername(User user, String username) {
    Toast.makeText(this, "Exist username" + username, Toast.LENGTH_LONG).show();
    showInsertUsername(user);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }
}
