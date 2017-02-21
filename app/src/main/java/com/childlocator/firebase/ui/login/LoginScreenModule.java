package com.childlocator.firebase.ui.login;

import android.support.v7.app.AlertDialog;

import com.childlocator.firebase.base.annotation.ActivityScope;
import com.childlocator.firebase.data.source.remote.FirebaseUserService;
import com.childlocator.firebase.data.source.remote.UserService;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginScreenModule {

  private LoginActivity activity;

  public LoginScreenModule(LoginActivity activity) {
    this.activity = activity;
  }

  @ActivityScope
  @Provides
  LoginActivity provideLoginActivity() {
    return activity;
  }

  @ActivityScope
  @Provides
  LoginScreenPresenter provideLoginPresenter(FirebaseUserService firebaseUserService, UserService userService) {
    return new LoginScreenPresenter(activity, firebaseUserService, userService);
  }

  @Provides
  @ActivityScope
  AlertDialog.Builder provideAlerDialogBuilder() {
    return new AlertDialog.Builder(activity);
  }
}
